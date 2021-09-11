package cn.vove7.android.scaffold.net

import cn.vove7.android.scaffold.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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