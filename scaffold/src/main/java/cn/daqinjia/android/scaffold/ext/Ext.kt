package cn.daqinjia.android.scaffold.ext

import android.os.Build
import android.os.Handler
import android.os.HandlerThread

/**
 * # Ext
 * Created on 2019/11/25
 *
 * @author Vove
 */

@JvmOverloads
fun runOnNewHandlerThread(
    name: String = "anonymous", delay: Long = 0,
    autoQuit: Boolean = true, block: () -> Unit): HandlerThread {

    return HandlerThread(name).apply {
        start()
        Handler(looper).postDelayed({
            block()
            if (autoQuit && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                quitSafely()
            else {
                quit()
            }
        }, delay)
    }
}
