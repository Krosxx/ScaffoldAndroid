package cn.daqinjia.android.scaffold.ui.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import cn.daqinjia.android.scaffold.R
import cn.daqinjia.android.scaffold.app.ActivityManager
import cn.daqinjia.android.scaffold.app.ActivityStatus

/**
 * # ScaffoldActivity
 *
 * - 支持自定义Toolbar
 * - 适配 DarkMode
 *
 * Created on 2019/12/13
 * @author Vove
 */
abstract class ScaffoldActivity<VDB : ViewDataBinding>
    : ScaffoldPage<VDB>, TextSizeableActivity() {

    override lateinit var _binding: ViewDataBinding

    //显示左侧导航图标
    open val showReturnIcon = true

    //需要显示Toolbar
    open val needToolbar = true

    override val textSizeChangeable: Boolean
        get() = supportFontSizeChangeable

    /**
     * 自定义 Toolbar 实现
     * Toolbar 实现 布局 参考 R.layout.toolbar_center_title
     */
    open val toolbarImpleRes = R.layout.toolbar_center_title

    //toolbar 实例
    lateinit var toolbar: Toolbar

    //
    companion object {
        //开启适配 DarkMode
        var enableThamable = false
        //Android Q DarkMode style 资源
        var darkTheme: Int = 0
        //支持字体大小可修改
        var supportFontSizeChangeable = false
    }

    private val isDarkMode: Boolean
        get() {
            val mode = resources!!.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return mode == Configuration.UI_MODE_NIGHT_YES
        }

    private val rootView by lazy {
        layoutInflater.inflate(R.layout.activity_scaffold, null) as ViewGroup
    }

    private fun checkTheme() {
        if (!enableThamable) {
            return
        }
        if (isDarkMode) {
            setTheme(darkTheme)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkTheme()
        super.onCreate(savedInstanceState)
        setCustomToolbar(rootView)
        buildView(rootView, layoutInflater)?.also {
            rootView.addView(it)
        }
        super.setContentView(rootView)

        onPageCreate()
    }

    override fun setContentView(view: View) {
        rootView.addView(view)
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, rootView, true)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        rootView.addView(view, params)
    }

    private fun setCustomToolbar(rootView: ViewGroup) {
        if (!needToolbar) {
            return
        }
        toolbar = (layoutInflater.inflate(
            toolbarImpleRes,
            rootView,
            true
        ) as ViewGroup).getChildAt(0) as Toolbar
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

    val status: ActivityStatus get() = ActivityManager[this] ?: ActivityStatus.STOPPED
}

abstract class NoBindingActivity : ScaffoldActivity<ViewDataBinding>()