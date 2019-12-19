package cn.daqinjia.android.scaffold.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * # ScaffoldApi
 * Created on 2019/12/18
 *
 * 网络请求封装
 *
 * @author Vove
 */
interface ScaffoldApi {

    val scope: CoroutineScope
    /**
     * 封装网络请求
     * 成功 req is
     */
    fun <T> apiCall(
        callAction: suspend () -> T,
        onResult: (Result<T>.() -> Unit)
    ) {
        scope.launch {
            try {
                val res = callAction()
                onResult(Result.success(res))
            } catch (e: Throwable) {
                onResult(Result.failure(e))
            }
        }
    }

}