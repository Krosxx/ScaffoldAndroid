package cn.daqinjia.android.scaffold.demo.activities

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.app.Api
import cn.daqinjia.android.scaffold.demo.data.ResponseData
import cn.daqinjia.android.scaffold.demo.databinding.ActivityNetTestBinding
import cn.daqinjia.android.scaffold.ext.viewModelOf
import cn.daqinjia.android.scaffold.ui.base.ScaffoldActivity
import cn.daqinjia.android.scaffold.ui.base.ScaffoldViewModel

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
 *
 *
 * @author Vove
 */
class NetTestActivity : ScaffoldActivity<ActivityNetTestBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_net_test

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
        apiCall(Api::get200) {
            onSuccess {
                resData.value = it
            }
            onFailure {
                emitUiState("loading_error" to it)
            }
        }
    }
}