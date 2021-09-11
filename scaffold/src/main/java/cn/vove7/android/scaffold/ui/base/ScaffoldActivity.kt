package cn.vove7.android.scaffold.ui.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import cn.vove7.android.scaffold.app.ActivityManager
import cn.vove7.android.scaffold.app.ActivityStatus

/**
 * # ScaffoldActivity
 *
 * - 支持自定义Toolbar
 * - 适配 DarkMode
 *
 * Created on 2019/12/13
 * @author Vove
 */
abstract class ScaffoldActivity<VDB : ViewBinding>
    : ScaffoldPage<VDB>, TextSizeableActivity() {

    override val layoutRes: Int = 0

    override lateinit var _binding: ViewBinding

    //显示左侧导航图标
    open val showReturnIcon = true

    override val textSizeChangeable: Boolean
        get() = supportFontSizeChangeable

    //toolbar 实例
    lateinit var toolbar: Toolbar

    //Activity 暗黑主题
    open val darkTheme: Int = globalDarkTheme

    //配置
    companion object {
        //开启适配 DarkMode
        var enableAutoDarkTheme = false

        //Android Q DarkMode style 资源
        var globalDarkTheme: Int = 0

        //支持字体大小可修改
        var supportFontSizeChangeable = false
    }

    private val isDarkMode: Boolean
        get() {
            val mode = resources!!.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return mode == Configuration.UI_MODE_NIGHT_YES
        }


    private fun buildScaffoldRootView(): ViewGroup? {
        return buildView(null, layoutInflater) as ViewGroup?
    }

    private fun checkTheme() {
        if (!enableAutoDarkTheme) {
            return
        }
        if (isDarkMode) {
            setTheme(darkTheme)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkTheme()
        super.onCreate(savedInstanceState)
        buildScaffoldRootView()?.also {
            super.setContentView(it)
        }
        onPageCreate()
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

abstract class NoBindingActivity : ScaffoldActivity<ViewBinding>()