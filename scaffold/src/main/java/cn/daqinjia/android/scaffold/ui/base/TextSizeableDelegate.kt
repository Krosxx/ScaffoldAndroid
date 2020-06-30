package cn.daqinjia.android.scaffold.ui.base

import android.content.Context
import android.content.res.Resources
import android.os.Build
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKey

/**
 * # TextSizeableDelegate
 * Activity 字体大小委托实现
 * @author Vove
 * 2019/7/29
 */
interface TextSizeableDelegate {

    @Config("base")
    companion object {
        var fontScale by smartKey(1f)
        var lastChangedTextSizeTime by smartKey(0L)
    }

    val textSizeChangeable: Boolean

    /**
     * Activity进入时间（加载资源时间，用于修改字体）
     */
    var loadTime: Long

    fun superAttachBaseContext(base: Context)

    /**
     * 安卓N以上
     * @param base Context
     */
    fun _attachBaseContext(base: Context) {
        loadTime = System.currentTimeMillis()
        if (textSizeChangeable && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val res = base.resources
            val config = res.configuration

            val scale = fontScale
            config.fontScale = scale
            val newContext = base.createConfigurationContext(config)
            superAttachBaseContext(newContext)
        } else {
            superAttachBaseContext(base)
        }
    }


    fun _onCreate() {
        loadTime = System.currentTimeMillis()
    }

    /**
     * 安卓N及以下
     *
     * @return Resources?
     */
    fun _getResources(sr: Resources?): Resources? {
        if (sr != null) {
            val scale = fontScale
            val config = sr.configuration
            if (config.fontScale != scale) {
                config.fontScale = scale
                sr.updateConfiguration(config, sr.displayMetrics)
            }
        }
        return sr
    }

    fun _onResume() {
        if (textSizeChangeable && loadTime < lastChangedTextSizeTime) {
            reStart()
        }
    }

    fun reStart()
}