package cn.daqinjia.android.scaffold.demo.activities

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.databinding.ActivityMultiStepBinding
import cn.daqinjia.android.scaffold.net.Api
import cn.daqinjia.android.scaffold.ui.base.ScaffoldActivity
import cn.daqinjia.android.scaffold.ui.base.ScaffoldViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * # DataFetchActivity
 * Created on 2019/11/25
 *
 * @author Vove
 */
class DataFetchActivity : ScaffoldActivity<ActivityMultiStepBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_multi_step

    private val vm: MStepViewModel by viewModel()

    override fun onObserveLiveData() {
        vm.uiData.observe(this) {
            Log.d("DATA", it["list"].toString())
        }
    }

    fun fetch(v: View) {
        vm.onFetch()
    }
}

class MStepViewModel : ScaffoldViewModel() {

    fun onFetch() = apiCall({ Api.list(0) }, "list")

}