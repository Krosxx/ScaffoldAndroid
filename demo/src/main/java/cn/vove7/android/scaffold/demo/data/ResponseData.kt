package cn.vove7.android.scaffold.demo.data

import cn.vove7.android.scaffold.ui.base.ScaffoldViewModel
import com.google.gson.annotations.SerializedName

/**
 * # BaseResponseData
 * Created on 2019/11/25
 *
 * 网络请求数据Model，[ScaffoldViewModel.apiCall]成功失败都将返回此对象，通过解析`isSuccess`区分请求结果
 *
 * @author Vove
 */
data class ResponseData<T>(
    @SerializedName("errCode", alternate = ["errcode"])
    val code: Int,
    val detail: String?,
    val data: T?
) {
    val isSuccess get() = code == 0
}
