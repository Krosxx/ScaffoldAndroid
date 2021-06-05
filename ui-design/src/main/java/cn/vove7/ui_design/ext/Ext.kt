package cn.vove7.ui_design.ext

import android.app.Application
import android.os.Handler
import android.os.Looper
import cn.vove7.android.common.log


val APPLICATION_INSTANCE: Application
    get() {
        val method = Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
        val ps = Array<Any?>(method.parameterTypes?.size ?: 0) { null }
        return method.invoke(null, *ps) as Application
    }

/**
 * 代码块运行于UI线程
 * @param action () -> Unit
 */
fun runOnUi(action: () -> Unit) {
    val mainLoop = Looper.getMainLooper()
    if (mainLoop == Looper.myLooper()) {
        action.invoke()
    } else {
        try {
            Handler(mainLoop).post(action)
        } catch (e: Exception) {
            e.log()
        }
    }
}

