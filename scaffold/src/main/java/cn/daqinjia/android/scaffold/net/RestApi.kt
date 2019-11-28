package cn.daqinjia.android.scaffold.net

import cn.daqinjia.android.scaffold.BuildConfig
import cn.daqinjia.android.scaffold.ext.ReflectExt.get
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

object RestApi {

    private var isDebug = true

    @JvmStatic
    fun debug(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    //创建Retrofit实例
    //后期可自定义timeout属性
    @Suppress("MemberVisibilityCanBePrivate")
    fun createApiClient(
        baseUrl: String,
        connectionTimeout: Int,
        readTimeout: Int,
        writeTimeout: Int
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(isDebug, connectionTimeout, readTimeout, writeTimeout))
            .build()
    }

    inline fun <reified T : Any> createKot(): T = createKot(T::class)

    // create api service baseUrl singleton
    // config value in companion
    fun <T : Any> createKot(
        clz: KClass<T>,
        connectionTimeout: Int = 5000,
        readTimeout: Int = 5000,
        writeTimeout: Int = 5000
    ): T {

        val companionRef = clz.companionObjectInstance
            ?: throw IllegalArgumentException("${clz.simpleName} does not have a companion field")

        return createApiClient(
            companionRef["BASE_URL"],
            companionRef["CONNECTIONTIMEOUT", connectionTimeout],
            companionRef["READTIMEOUT", readTimeout],
            companionRef["WRITETIMEOUT", writeTimeout]
        ).create(clz.java)
    }

    // create okHttpClient singleton
    private fun createOkHttpClient(
        debug: Boolean, connectionTimeout: Int,
        readTimeout: Int, writeTimeout: Int
    ): OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG && debug) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
//                .addNetworkInterceptor(HttpCacheInterceptor())
//                .addInterceptor(CustomHeaderInterceptor())
            connectTimeout(connectionTimeout.toLong(), TimeUnit.MILLISECONDS)
            readTimeout(readTimeout.toLong(), TimeUnit.MILLISECONDS)
            writeTimeout(writeTimeout.toLong(), TimeUnit.MILLISECONDS)
        }.build()


}
