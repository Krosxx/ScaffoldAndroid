package cn.daqinjia.android.scaffold.net

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

/**
 * # CallbackBuilder
 * retrofit2 回调
 * @author Vove
 * 2019/8/7
 */
class CallbackBuilder<T>(
    lcPair: Pair<Lifecycle?, Call<T>>? = null,
    builder: CallbackBuilder<T>.() -> Unit
) : Callback<T>, LifecycleObserver {
    init {
        apply(builder)
        val lc = lcPair?.first
        if (lc != null) {
            registerLifeCircle(lc, lcPair.second)
        }
    }

    fun onSuccess(action: OnSuccessAction<T>) {
        successAction = action
    }

    fun onFailure(action: OnFailureAction<T>) {
        failAction = action
    }

//    fun toastOnFailure() {
//        onFailure { _, _ ->
//            UIHelper.showNetWarning(MainApp.getContext())
//        }
//    }

    /**
     * 结束事件，无论成败，取消请求后不再通知
     * @param finish Function0<Unit>
     */
    fun onFinish(finish: () -> Unit) {
        onFinish = finish
    }

    private var successAction: OnSuccessAction<T>? = null
    private var failAction: OnFailureAction<T>? = null
    private var onFinish: (() -> Unit)? = null


    override fun onFailure(call: Call<T>?, t: Throwable?) {
        //此处判断是否取消请求异常
        if (destroyed) {
            return
        }
        callFailed(call, t ?: Exception("未知错误"))
        callFinish()
    }

    /**
     * 此处 状态码200-300 视为成功
     * @param call Call<T>
     * @param response Response<T>
     */
    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        if (destroyed) {
            return
        }
        val data = response?.body()
        if (response?.isSuccessful == true && data != null) {
            callSuccess(call, data)
        } else {
            val jsonStr = response?.errorBody()?.string()
            val data = try {
                JsonParser().parse(jsonStr).asJsonObject
            } catch (e: Throwable) {
                null
            }
            callFailed(call, UnSuccessfulRequestException(response?.code() ?: -1, data))
        }
        callFinish()
    }

    private var lifecycle: Lifecycle? = null
    private var weakCall: WeakReference<Call<T>>? = null

    /**
     * 监听Activity生命周期
     *
     * @param lc Lifecycle
     * @param call Call<T>
     */
    private fun registerLifeCircle(lc: Lifecycle, call: Call<T>) {
        this.lifecycle = lc
        lc.addObserver(this)
        weakCall = WeakReference(call)
    }

    var destroyed = false

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        //被摧毁
        destroyed = true
        weakCall?.get()?.cancel()
        lifecycle?.removeObserver(this)
        lifecycle = null
    }

    private fun callSuccess(call: Call<T>?, data: T) = GlobalScope.launch(Dispatchers.Main) {
        successAction?.invoke(call, data)
    }

    private fun callFailed(call: Call<T>?, t: Throwable) = GlobalScope.launch(Dispatchers.Main) {
        failAction?.invoke(call, t)
    }

    private fun callFinish() = GlobalScope.launch(Dispatchers.Main) {
        onFinish?.invoke()
    }
}

class UnSuccessfulRequestException(val statusCode: Int, val data: JsonObject?) : Exception()

typealias OnFailureAction<T> = (_: Call<T>?, t: Throwable) -> Unit

typealias OnSuccessAction<T> = (_: Call<T>?, data: T) -> Unit

/**
 *
 * @receiver Call<T>
 * @param lc Lifecycle?
 * @param builder [@kotlin.ExtensionFunctionType] Function1<CallbackBuilder<T>, Unit>
 */
fun <T> Call<T>.enqueue(lc: Lifecycle? = null, builder: CallbackBuilder<T>.() -> Unit) {
    enqueue(CallbackBuilder(Pair(lc, this), builder))
}

/**
 * 忽略请求失败
 *
 * @receiver Call<T>
 * @param lc Lifecycle?
 * @param onSuccessAction Function2<[@kotlin.ParameterName] Call<T>?, [@kotlin.ParameterName] T, Unit>
 */
fun <T> Call<T>.start(lc: Lifecycle? = null, onSuccessAction: OnSuccessAction<T>) {
    val builder: (CallbackBuilder<T>.() -> Unit) = {
        onSuccess(onSuccessAction)
    }
    enqueue(CallbackBuilder(Pair(lc, this), builder))
}
