package cn.vove7.android.scaffold.demo.activities

import android.annotation.SuppressLint
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.vove7.android.scaffold.demo.R
import cn.vove7.android.scaffold.demo.app.AppDatabase
import cn.vove7.android.scaffold.demo.data.get
import cn.vove7.android.scaffold.demo.data.set
import cn.vove7.android.scaffold.demo.databinding.ActivityMvvmDemoBinding
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MVVMDemoActivity : ScaffoldActivity<ActivityMvvmDemoBinding>() {

    //koin fun viewModel() 注入repo
    private val vm: DemoViewModel by viewModel()

    @SuppressLint("SetTextI18n")
    override fun onObserveLiveData() {
        vm.number.observe(this) {
            binding.a.text = it.toString()
            vm.updateUserName("Vove-$it")
        }
        vm.username.observe(this) {
            binding.b.text = "local name: " + it.value
        }
    }

    fun onClick(v: View) {
        vm.add()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.simple, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, item.title ?: "null", Toast.LENGTH_SHORT).show()

        return super.onOptionsItemSelected(item)
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
