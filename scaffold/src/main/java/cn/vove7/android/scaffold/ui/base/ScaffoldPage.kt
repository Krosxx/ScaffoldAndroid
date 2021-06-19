package cn.vove7.android.scaffold.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import cn.vove7.android.common.logi
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.*


@Suppress("UNCHECKED_CAST")
interface ScaffoldPage<VDB : ViewBinding> {

    fun getPageViewBindingCls(): Class<VDB>? {

        var superCls = this.javaClass.genericSuperclass
        while (superCls !is ParameterizedType) {
            superCls = (superCls as Class<*>).genericSuperclass
        }
        val vbType = superCls.actualTypeArguments[0] as Class<*>

        if (vbType == ViewBinding::class.java) {
            return null
        }
        return vbType as Class<VDB>
    }

    var _binding: ViewBinding

    val binding: VDB get() = _binding as VDB

    val layoutRes: Int

    fun buildView(container: ViewGroup? = null, layoutInflater: LayoutInflater): View? {
        val inflateView by lazy {
            //支持 不指定布局资源
            if (layoutRes == 0) null
            else layoutInflater.inflate(layoutRes, container, false)
        }
        return getPageViewBindingCls()?.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )?.let { m ->
            _binding = m.invoke(null, layoutInflater, container, false) as VDB
            _binding.root
        } ?: inflateView
    }

    fun onObserveLiveData() {}
    fun bindBinding() {}
    fun initView() {}

    fun onPageCreate() {
        bindBinding()
        initView()
        onObserveLiveData()
    }


}