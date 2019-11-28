package cn.daqinjia.android.scaffold.app

import android.app.Application
import cn.daqinjia.android.scaffold.ext.runOnNewHandlerThread

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
        runOnNewHandlerThread(delay = 2000) { delayInit() }
    }

    open fun immediatelyInit() {}
    open fun delayInit() {}

}
