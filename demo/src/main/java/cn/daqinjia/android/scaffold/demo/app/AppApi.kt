package cn.daqinjia.android.scaffold.demo.app

import cn.daqinjia.android.scaffold.net.ResponseData
import cn.daqinjia.android.scaffold.net.RestApi
import cn.daqinjia.android.scaffold.net.RetrofitApiConstants
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * # AppApi
 * Created on 2019/11/25
 *
 * @author Vove
 */
interface AppApi {


    companion object : RetrofitApiConstants {
        override val baseUrl: String = "http://192.168.1.103"
        override val connectionTimeoutMillis: Int = 3000
        override val readTimeoutMillis: Int = 3000
        override val writeTimeoutMillis: Int = 3000
    }

    @GET("/200")
    suspend fun get200(): ResponseData<String>

    @GET("/list")
    suspend fun list(@Query("page") page: Int): ResponseData<Array<Int>>

}

val Api by lazy { RestApi.createKot<AppApi>() }
