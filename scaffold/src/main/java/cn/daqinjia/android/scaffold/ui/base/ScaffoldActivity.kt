package cn.daqinjia.android.scaffold.ui.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding


abstract class ScaffoldActivity<VDB : ViewDataBinding> : ScaffoldPage<VDB>, AppCompatActivity(),
    TextSizeableDelegate {
    override lateinit var _binding: ViewDataBinding
    override var loadTime: Long = 0

    open val showReturnIcon = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _onCreate()
        setContentView(buildView(null, layoutInflater))
        if (showReturnIcon) {
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }
        onPageCreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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