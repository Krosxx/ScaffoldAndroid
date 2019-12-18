package cn.daqinjia.android.scaffold.ui.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import cn.daqinjia.android.scaffold.R
import kotlinx.android.synthetic.main.activity_scaffold.*


abstract class ScaffoldActivity<VDB : ViewDataBinding>
    : ScaffoldPage<VDB>, TextSizeableActivity() {

    override lateinit var _binding: ViewDataBinding

    open val showReturnIcon = true

    open val needToolbar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = layoutInflater.inflate(R.layout.activity_scaffold, null) as ViewGroup
        rootView.addView(buildView(rootView, layoutInflater))
        setContentView(rootView)
        setCustomToolbar()

        onPageCreate()
    }

    private fun setCustomToolbar() {
        if (!needToolbar) {
            return
        }
        toolbar?.apply {
            setSupportActionBar(this)
            if (showReturnIcon) {
                setNavigationIcon(R.drawable.back_arrow)
                setNavigationOnClickListener { onBackPressed() }
            }
        }
    }

    final override fun onPageCreate() {
        super.onPageCreate()
    }

}

abstract class NoBindingActivity : ScaffoldActivity<ViewDataBinding>()