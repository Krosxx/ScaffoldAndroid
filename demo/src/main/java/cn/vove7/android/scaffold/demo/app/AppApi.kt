package cn.vove7.android.scaffold.demo.app

import cn.vove7.android.scaffold.demo.data.ResponseData
import cn.vove7.android.scaffold.net.RestApi
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * # AppApi
 * Created on 2019/11/25
 *
 * @author Vove
 */
interface AppApi {

    @GET("/200")
    suspend fun get200(): ResponseData<String>

    @GET("/list")
    suspend fun list(@Query("page") page: Int): ResponseData<Array<Int>>

    companion object : AppApi by Api {
        const val baseUrl: String = "http://192.168.1.103"
        const val connectionTimeoutMillis: Int = 3000
        const val readTimeoutMillis: Int = 3000
        const val writeTimeoutMillis: Int = 3000
    }
}

private val Api by lazy {
    RestApi.createApiClient(
        AppApi.baseUrl, AppApi.connectionTimeoutMillis,
        AppApi.readTimeoutMillis,
        AppApi.writeTimeoutMillis,
    ).create(AppApi::class.java)
}
