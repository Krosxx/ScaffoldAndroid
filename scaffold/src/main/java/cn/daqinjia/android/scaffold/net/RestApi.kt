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

    /**
     * Create api service baseUrl singleton
     *
     * Config value in companion impl [RetrofitApiConstants]
     *
     * 参数优先级高于 Api 内部定义参数
     */
    fun <T : Any> createKot(
        clz: KClass<T>,
        connectionTimeout: Int? = null,
        readTimeout: Int? = null,
        writeTimeout: Int? = null
    ): T {

        val companionRef = clz.companionObjectInstance
            ?: throw IllegalArgumentException("${clz.simpleName} does not have a companion field")

        return if (companionRef is RetrofitApiConstants) {
            createApiClient(
                companionRef.baseUrl,
                connectionTimeout ?: companionRef.connectionTimeoutMillis,
                readTimeout ?: companionRef.readTimeoutMillis,
                writeTimeout ?: companionRef.writeTimeoutMillis
            ).create(clz.java)
        } else {
            createApiClient(
                companionRef["BASE_URL"],
                connectionTimeout ?: companionRef["CONNECTIONTIMEOUT", 5000],
                readTimeout ?: companionRef["READTIMEOUT", 5000],
                writeTimeout ?: companionRef["WRITETIMEOUT", 5000]
            ).create(clz.java)
        }
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

interface RetrofitApiConstants {
    val baseUrl: String
    val connectionTimeoutMillis: Int
    val readTimeoutMillis: Int
    val writeTimeoutMillis: Int
}