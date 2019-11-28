package cn.daqinjia.android.scaffold.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.daqinjia.android.scaffold.net.BaseResponseData
import kotlinx.coroutines.launch

/**
 * # ScaffoldViewModel
 * Created on 2019/11/26
 *
 * @author Vove
 */
typealias UiData = Map<String, Any?>

open class ScaffoldViewModel : ViewModel() {

    /**
     * 负责传递数据到UI
     */
    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData> get() = _uiData

    /**
     * 通知UI
     */
    fun emitUiState(vararg data: Pair<String, Any?>) {
        _uiData.value = mapOf(*data)
    }

    /**
     * 网络请求 -> LiveData
     * 通过更新LiveData
     */
    fun <T> apiCall(
        callAction: suspend () -> BaseResponseData<T>,
        ld: MutableLiveData<*>,
        onSuccess: (() -> Unit)? = null
    ) {
        apiCall(callAction) {
            ld.value = it
            if (it.isSuccess) {
                onSuccess?.invoke()
            }
        }
    }

    /**
     *
     */
    fun <T> apiCall(
        callAction: suspend () -> BaseResponseData<T>,
        reqName: String,
        onSuccess: (() -> Unit)? = null
    ) {
        apiCall(callAction) {
            emitUiState(reqName to it)
            if (it.isSuccess) {
                onSuccess?.invoke()
            }
        }
    }

    /**
     * 封装网络请求
     * 成功 req is
     */
    fun <T> apiCall(
        callAction: suspend () -> BaseResponseData<T>,
        onResult: ((BaseResponseData<T>) -> Unit)
    ) {
        viewModelScope.launch {
            try {
                val res = callAction()
                onResult(res)
            } catch (e: Throwable) {
                val baseReq = BaseResponseData<T>(-9, e.message, null, e)
                onResult(baseReq)
            }
        }
    }
}
