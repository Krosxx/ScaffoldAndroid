package cn.daqinjia.android.scaffold.ui.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding


abstract class ScaffoldActivity<VDB : ViewDataBinding> : ScaffoldPage<VDB>, AppCompatActivity(),
    TextSizeableDelegate {
    override lateinit var _binding: ViewDataBinding
    override var loadTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _onCreate()
        setContentView(buildView(null, layoutInflater))
        onPageCreate()
    }

    override fun superAttachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun attachBaseContext(newBase: Context) {
        _attachBaseContext(newBase)
    }

    override fun getResources(): Resources? {
        return _getResources(super.getResources())
    }

    override fun reStart() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    final override fun onPageCreate() {
        super.onPageCreate()
    }

    override fun onResume() {
        super.onResume()
        _onResume()
    }
}

abstract class NoBindingActivity : ScaffoldActivity<ViewDataBinding>()