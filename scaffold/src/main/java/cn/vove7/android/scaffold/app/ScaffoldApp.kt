package cn.vove7.android.scaffold.app

import android.app.Application
import cn.vove7.android.common.ext.delayRun

/**
 * # ScaffoldApp
 * Created on 2019/11/25
 *
 * @author Vove
 */
abstract class ScaffoldApp : Application() {
    companion object {
        lateinit var APP: Application
    }

    override fun onCreate() {
        APP = this
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityManager)
        immediatelyInit()
        delayRun(2000) { delayInit() }
    }

    open fun immediatelyInit() {}
    open fun delayInit() {}

}
