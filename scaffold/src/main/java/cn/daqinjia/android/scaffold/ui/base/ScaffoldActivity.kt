package cn.daqinjia.android.scaffold.ui.base

import android.os.Build
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
                setNavigationOnClickListener {
                    onBackPressed()
                }
            }
        }
    }

    final override fun onPageCreate() {
        super.onPageCreate()
    }

    var backToParentPage = true
    /**
     * 若指定 parentActivity 则启动
     * 解决在启动某些Activity后，返回无法回到主页
     * 若无需跳转，backToParentPage = false
     */
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && backToParentPage) {
            parentActivityIntent?.also {
                startActivityIfNeeded(it, 0)
            }
        }
        super.onBackPressed()
    }
}

abstract class NoBindingActivity : ScaffoldActivity<ViewDataBinding>()