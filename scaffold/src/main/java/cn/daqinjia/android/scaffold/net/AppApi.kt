package cn.daqinjia.android.scaffold.net

import androidx.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * # AppApi
 * Created on 2019/11/25
 *
 * @author Vove
 */
interface AppApi {


    companion object {

        @JvmStatic
        val BASE_URL = "http://192.168.1.103"
    }

    @GET("/200")
    suspend fun get200(): BaseResponseData<String>

    @GET("/list")
    suspend fun list(@Query("page") page: Int): BaseResponseData<Array<Int>>

}

val Api by lazy { RestApi.createKot<AppApi>() }
