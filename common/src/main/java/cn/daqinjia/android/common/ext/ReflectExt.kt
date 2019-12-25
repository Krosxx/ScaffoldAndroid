@file:Suppress("UNCHECKED_CAST")

package cn.daqinjia.android.common.ext

import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties

/**
 * # ReflectExt
 * 反射扩展函数
 * @author Vove
 * 2019/7/29
 */
object ReflectExt {

    /**
     * 属性反射委托
     * - 支持反射父类
     *
     * 使用：
     *```
     * var s by this.field<Int>("mSolidColor")
     * s = Color.parseColor("#FFD93830")
     *```
     * @receiver T
     * @param field String 属性名
     * @return FiledDelegate
     */
    inline fun <reified T> Any.field(
        field: String,
        defaultIfNotFound: T? = null
    ): FieldDelegate<T> {
        return FieldDelegate(
            this,
            this::class.java,
            field,
            defaultIfNotFound
        )
    }

    /**
     * 反射Kotlin类成员
     *
     * Companion 成员：
     * val a by ApiInterface::class.companionObjectInstance!!.property<String>("BASE_URL")
     * @receiver Any
     * @param name String
     * @return KMemberDelegate<T>
     */
    inline fun <reified T> Any.property(
        name: String,
        defaultIfNotFound: T? = null
    ): KPropertyDelegate<T> {
        return KPropertyDelegate(
            this,
            this::class,
            name,
            defaultIfNotFound
        )
    }

    inline operator fun <reified T> Any.get(name: String, defaultIfNotFound: T? = null): T {
        return if (this is Class<*>) {
            @Suppress("RemoveExplicitTypeArguments")
            val value by staticField<T>(name, defaultIfNotFound)
            value
        } else getPropertyValue(name, defaultIfNotFound)
    }

    inline fun <reified T> Any.getPropertyValue(name: String, defaultIfNotFound: T? = null): T {
        val cTimeoutField by KPropertyDelegate(
            this,
            this::class,
            name,
            defaultIfNotFound
        )
        return cTimeoutField
    }


    inline fun <reified T> Class<*>.staticField(
        field: String,
        defaultIfNotFound: T? = null
    ): FieldDelegate<T> {
        return FieldDelegate(null, this, field, defaultIfNotFound)
    }

    fun Any.method(method: String, vararg argTypes: Class<*>): MethodDelegate {
        return MethodDelegate(
            this,
            this::class.java,
            method,
            *argTypes
        )
    }
}

/**
 * 函数委托
 * @property ref Any?
 * @property cls Class<*>
 * @property methodName String
 * @property method Method
 * @constructor
 */
class MethodDelegate(
    private val ref: Any?,
    private val cls: Class<*>,
    private val methodName: String,
    vararg argsType: Class<*>
) {
    private val method: Method = {
        var m: Method? = null
        var _cls = cls

        while (_cls != Any::class.java) {
            try {
                m = _cls.getDeclaredMethod(methodName, *argsType)
                break
            } catch (e: Exception) {
            }
            _cls = _cls.superclass as Class<*>
        }
        m ?: throw NoSuchMethodException(methodName)
        m
    }()

    operator fun invoke(vararg args: Any?) {
        method.isAccessible = true
        method.invoke(ref, *args)
    }
}

/**
 * Kotlin 类属性委托
 * @param T
 * @property ref Any
 * @property cls KClass<*>
 * @property propertyName String
 * @property property KProperty<*>
 * @constructor
 */
class KPropertyDelegate<T>(
    private val ref: Any,
    private val cls: KClass<*>,
    private val propertyName: String,
    private val defaultIfNotFound: T?
) {
    private val property: KProperty<*>? by lazy {
        val f = cls.declaredMemberProperties.find { it.name == propertyName }
        if (f == null && defaultIfNotFound == null) {
            throw NoSuchFieldException("$propertyName in ${cls.simpleName}")
        }
        f
    }

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        if (property == null && defaultIfNotFound != null) return defaultIfNotFound

        val getter =
            property?.getter ?: throw NoSuchFieldException("$propertyName in ${cls.simpleName}")

        //当属性为const，不需传this, 适配接口companion 类
        return try {
            getter.call()
        } catch (e: IllegalArgumentException) {
            getter.call(ref)
        } catch (e: Throwable) {
            defaultIfNotFound ?: throw e
        } as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T?) {
        val p = this.property
        if (p is KMutableProperty<*>) {
            return p.setter.call(ref, t)
        } else throw IllegalAccessException("${property.name} is not a mutable value")
    }
}

/**
 * 属性委托
 * @param T
 * @property ref Any?
 * @property cls Class<*>
 * @property fieldName String
 * @property field Field
 * @constructor
 */
class FieldDelegate<T>(
    private val ref: Any?,
    private val cls: Class<*>,
    private val fieldName: String,
    private val defaultIfNotFound: T?
) {

    private val field: Field by lazy {
        var field: Field? = null

        var _cls = cls

        while (_cls != Any::class.java) {
            try {
                field = _cls.getDeclaredField(fieldName)
                break
            } catch (e: Exception) {
            }
            _cls = _cls.superclass ?: throw NoSuchFieldException(fieldName)
        }
        field?.also {
            it.isAccessible = true
        } ?: throw NoSuchFieldException(fieldName)
    }

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        return try {
            field.get(ref) as T
        } catch (e: NoSuchFieldException) {
            defaultIfNotFound ?: throw e
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T?) {
        return field.set(ref, t)
    }
}