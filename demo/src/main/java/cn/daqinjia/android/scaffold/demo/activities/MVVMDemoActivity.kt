package cn.daqinjia.android.scaffold.demo.activities

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.app.AppDatabase
import cn.daqinjia.android.scaffold.demo.data.get
import cn.daqinjia.android.scaffold.demo.data.set
import cn.daqinjia.android.scaffold.demo.databinding.ActivityMvvmDemoBinding
import cn.daqinjia.android.scaffold.ui.base.ScaffoldActivity
import org.koin.android.viewmodel.ext.android.viewModel

class MVVMDemoActivity : ScaffoldActivity<ActivityMvvmDemoBinding>() {
    override val layoutRes: Int get() = R.layout.activity_mvvm_demo

    //koin fun viewModel() 注入repo
    private val vm: DemoViewModel by viewModel()

    override fun onObserveLiveData() {
        vm.number.observe(this) {
            binding.number = it
            vm.updateUserName("Vove-$it")
        }
        vm.username.observe(this) {
            binding.username = "local name: " + it.value
        }
    }

    override fun bindBinding() {
        binding.number = vm.number.value ?: 0
    }

    fun onClick(v: View) {
        vm.add()
    }
}

class DemoViewModel : ViewModel() {
    init {
        Log.d("DemoViewModel", "init")
    }

    val number = MutableLiveData(11)

    fun add() {
        number.value = (number.value ?: 0) + 1
    }

    fun updateUserName(n: String) {
        AppDatabase.configdDao["username"] = n
    }

    val username = AppDatabase.configdDao["username"]
}
