package cn.daqinjia.android.scaffold.demo.activities

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.databinding.ActivityNetTestBinding
import cn.daqinjia.android.scaffold.ext.viewModelOf
import cn.daqinjia.android.scaffold.demo.app.Api
import cn.daqinjia.android.scaffold.net.BaseResponseData
import cn.daqinjia.android.scaffold.ui.base.ScaffoldActivity
import cn.daqinjia.android.scaffold.ui.base.ScaffoldViewModel

/**
 * # NetTestActivity
 * Created on 2019/11/25
 *
 * 网络请求测试
 * @author Vove
 */
class NetTestActivity : ScaffoldActivity<ActivityNetTestBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_net_test

    private val vm by viewModelOf<NetTestViewModel>()

    override fun onObserveLiveData() {
        vm.resData.observe(this, Observer {
            Log.d("NetTest", it.toString())
            if (it.isSuccess) {
                binding.data = it.data
            } else {
                binding.error = true
            }
        })
    }
}

class NetTestViewModel : ScaffoldViewModel() {
    val resData = MutableLiveData<BaseResponseData<String>>()

    init {
        apiCall(Api::get200, resData)
    }
}