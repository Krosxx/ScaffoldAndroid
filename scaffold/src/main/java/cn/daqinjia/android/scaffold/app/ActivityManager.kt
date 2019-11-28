package cn.daqinjia.android.scaffold.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlin.reflect.KClass

/**
 * # ActivityManager
 * 管理Activity状态
 * Created on 2019/11/25
 *
 * Activity LifeCycle
 * ![](https://img-my.csdn.net/uploads/201109/1/0_1314838777He6C.gif)
 *
 * @author Vove
 */
object ActivityManager : Application.ActivityLifecycleCallbacks {

    private val activities = HashMap<Activity, ActivityStatus>()

    operator fun contains(activity: Activity) = activity in activities

    operator fun get(actCls: Class<*>): Activity? =
        activities.keys.find { it::class.java == actCls }

    operator fun get(actCls: KClass<*>): Activity? =
        activities.keys.find { it::class == actCls }

    override fun onActivityPaused(activity: Activity) {
        activities[activity] = ActivityStatus.PAUSED
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities -= activity
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
        activities[activity] = ActivityStatus.STOPPED
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        activities[activity] = ActivityStatus.SHOWING
    }
}

enum class ActivityStatus {
    SHOWING, STOPPED, PAUSED,
}