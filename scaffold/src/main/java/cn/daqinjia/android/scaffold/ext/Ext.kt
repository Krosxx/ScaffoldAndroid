package cn.daqinjia.android.scaffold.ext

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * # Ext
 * Created on 2019/11/25
 *
 * @author Vove
 */

fun delayRun(millis: Long, block: () -> Unit): Job = GlobalScope.launch {
    delay(millis)
    block()
}
