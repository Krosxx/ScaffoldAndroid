package cn.vove7.android.scaffold.demo.activities

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import cn.vove7.android.scaffold.demo.app.AppApi
import cn.vove7.android.scaffold.demo.data.ResponseData
import cn.vove7.android.scaffold.demo.databinding.ActivityNetTestBinding
import cn.vove7.android.scaffold.ext.viewModelOf
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import cn.vove7.android.scaffold.ui.base.ScaffoldViewModel

/**
 * # NetTestActivity
 * Created on 2019/11/25
 *
 * 网络请求测试
 *
 *
 * 初始加载数据多种情形：
 *
 * 1. 进入加载失败，显示重试按钮
 *
 * @author Vove
 */
class NetTestActivity : ScaffoldActivity<ActivityNetTestBinding>() {

    private val vm by viewModelOf<NetTestViewModel>()

    override fun onObserveLiveData() {
        vm.uiData.observe(this) {
            if ("loading" in it) {
                binding.error = false
            }
            if ("loading_error" in it) {
                binding.error = true
            }
        }

        vm.resData.observe(this) {
            Log.d("NetTest", it.toString())
            if (it.isSuccess) {
                binding.data = it.data
            } else {
                binding.error = true
            }
        }
    }

    fun reload(v: View) {
        vm.load()
    }
}

class NetTestViewModel : ScaffoldViewModel() {
    val resData = MutableLiveData<ResponseData<String>>()

    /**
     * 在第一次页面加载时触发
     * 可以对数据进行缓存（重建时无需再请求数据）
     */
    init {
        load()
    }

    fun load() {
        emitUiState("loading")
        apiCall(AppApi::get200) {
//            此处非回调，等价于：
//            if (isSuccess) {
//
//            } else {
//
//            }
            onSuccess {
                resData.value = it
            }
            onFailure {
                emitUiState("loading_error" to it)
            }
        }
    }
}