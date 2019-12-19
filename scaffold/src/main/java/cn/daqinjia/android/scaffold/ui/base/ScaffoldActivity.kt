package cn.daqinjia.android.scaffold.ui.base

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import cn.daqinjia.android.scaffold.R


abstract class ScaffoldActivity<VDB : ViewDataBinding>
    : ScaffoldPage<VDB>, TextSizeableActivity() {

    override lateinit var _binding: ViewDataBinding

    open val showReturnIcon = true

    open val needToolbar = true

    /**
     * 可自定义 Toolbar 实现
     * Toolbar 实现 布局 参考 R.layout.toolbar_center_title
     */
    open val toolbarImpleRes = R.layout.toolbar_center_title

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = layoutInflater.inflate(R.layout.activity_scaffold, null) as ViewGroup
        setCustomToolbar(rootView)
        rootView.addView(buildView(rootView, layoutInflater))
        setContentView(rootView)

        onPageCreate()
    }

    private fun setCustomToolbar(rootView: ViewGroup) {
        if (!needToolbar) {
            return
        }
        toolbar = (layoutInflater.inflate(toolbarImpleRes, rootView, true) as ViewGroup).getChildAt(0) as Toolbar
        toolbar.apply {
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
                startActivity(it)
            }
        }
        super.onBackPressed()
    }
}

abstract class NoBindingActivity : ScaffoldActivity<ViewDataBinding>()