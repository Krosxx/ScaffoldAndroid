package cn.vove7.android.scaffold.demo.activities

import android.view.View
import androidx.lifecycle.viewModelScope
import cn.vove7.android.scaffold.demo.app.AppApi
import cn.vove7.android.scaffold.demo.databinding.ActivityDataFetchBinding
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import cn.vove7.android.scaffold.ui.base.ScaffoldViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * # DataFetchActivity
 * Created on 2019/11/25
 *
 * @author Vove
 */
class DataFetchActivity : ScaffoldActivity<ActivityDataFetchBinding>() {

    private val vm: MStepViewModel by viewModel()

    override fun onObserveLiveData() {
        vm.uiData.observe(this) {
            binding.dataView.text = it["fetch_data"].toString()
        }
    }

    fun fetch(v: View) {
        binding.dataView.text = "fetching..."
        vm.onFetch()
    }
}

val IT = fun(it: Any?): Any? = it

class MStepViewModel : ScaffoldViewModel() {

    //通过 reqName 作为请求标识 => uiData(LiveData) => UI
    fun onFetch() = viewModelScope.launch {
        emitUiState(
            "fetch_data" to kotlin.runCatching {
                AppApi.list(0)
            }.fold(onSuccess = IT, onFailure = { it.message })
        )
    }

}