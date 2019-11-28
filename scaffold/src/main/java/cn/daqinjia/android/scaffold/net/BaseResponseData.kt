package cn.daqinjia.android.scaffold.net

import com.google.gson.annotations.SerializedName

/**
 * # BaseResponseData
 * Created on 2019/11/25
 *
 * @author Vove
 */
data class BaseResponseData<T>(
    @SerializedName("errCode", alternate = ["errcode"])
    val code: Int,
    val detail: String?,
    val data: T?,
    val error: Throwable? = null
) {
    val isSuccess get() = code == 0 && error == null


}
