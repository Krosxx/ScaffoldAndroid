package cn.daqinjia.android.scaffold.demo.activities

import android.view.View
import androidx.lifecycle.observe
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.app.Api
import cn.daqinjia.android.scaffold.demo.databinding.ActivityDataFetchBinding
import cn.daqinjia.android.scaffold.ui.base.ScaffoldActivity
import cn.daqinjia.android.scaffold.ui.base.ScaffoldViewModel
import kotlinx.android.synthetic.main.activity_data_fetch.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * # DataFetchActivity
 * Created on 2019/11/25
 *
 * @author Vove
 */
class DataFetchActivity : ScaffoldActivity<ActivityDataFetchBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_data_fetch

    private val vm: MStepViewModel by viewModel()

    override fun onObserveLiveData() {
        vm.uiData.observe(this) {
            data_view.text = it["fetch_data"].toString()
        }
    }

    fun fetch(v: View) {
        data_view.text = "fetching..."
        vm.onFetch()
    }
}

class MStepViewModel : ScaffoldViewModel() {

    //通过 reqName 作为请求标识 => uiData(LiveData) => UI
    fun onFetch() = apiCall({ Api.list(0) }, "fetch_data")

}