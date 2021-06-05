package cn.vove7.android.common

import android.util.Log
import kotlin.math.min
import kotlin.properties.Delegates.observable

/**
 * # Logger
 *
 * 配置：
 * ```kotlin
 * Logger {
 *     outputLevel = if(BuildConfig.DEBUG) Log.VERBOSE else 100
 *     callstackDepth = 3
 * }
 * ```
 *
 * Created on 2019/12/18
 * @author Vove
 */
object Logger {

    //初始化 dsl
    operator fun invoke(action: Logger.() -> Unit) = apply(action)

    /**
     * 打印函数调用栈深度
     */
    var callstackDepth = 3
        set(value) {
            if (value >= 0) {
                field = min(8, value)
            } else {
                throw IllegalArgumentException("callstackDepth must >= 0")
            }
        }

    var outputLevel = Log.VERBOSE

    var tag = ">>>"

    /**
     * @param stackOffset 为了封装函数，能够正确输出函数栈
     */
    fun logv(stackOffset: Int = 0, msg: () -> Any?) = log(Log.VERBOSE, stackOffset, msg)

    fun logd(stackOffset: Int = 0, msg: () -> Any?) = log(Log.DEBUG, stackOffset, msg)
    fun logi(stackOffset: Int = 0, msg: () -> Any?) = log(Log.INFO, stackOffset, msg)
    fun logw(stackOffset: Int = 0, msg: () -> Any?) = log(Log.WARN, stackOffset, msg)
    fun loge(stackOffset: Int = 0, msg: () -> Any?) = log(Log.ERROR, stackOffset, msg)

    fun log(level: Int, stackOffset: Int, msg: () -> Any?) {
        needPrint(level) ?: return
        val m = msg().toString()
        listeners.forEach { it(level, m) }
        Log.println(level, tag, topLine)
        printCallStack(level, stackOffset)
        m.lineSequence().forEach {
            Log.println(level, tag, "$linePre$it")
        }
        Log.println(level, tag, bottomLine)
    }

    /**
     * 是否输出此日志
     * @return null 不输出，not null 输出
     */
    internal fun needPrint(l: Int): Unit? = if (l < outputLevel) null else Unit

    private fun printCallStack(level: Int, stackOffset: Int) {
        val st = Thread.currentThread().stackTrace
        runCatching {
            (callstackDepth - 1 downTo 0).forEach { i ->
                Log.println(level, tag, st[stackOffset + 6 + i].string())
            }
        }
    }

    private fun StackTraceElement.string(): String = buildString {
        append(linePre)
        append(methodName)
        append('(')
        append(fileName)
        append(':')
        append(lineNumber)
        append(')')
    }

    fun listen(lis: LogListener) {
        listeners += lis
    }

    fun removeListener(lis: LogListener) {
        listeners -= lis
    }

    private val listeners = mutableSetOf<LogListener>()


    var linePre = "│ "

    var topPre by observable("╭") { _, _, _ ->
        refreshTopLine()
    }

    var topSeq by observable("─") { _, _, _ ->
        refreshTopLine()
    }
    var topTail by observable("╮") { _, _, _ ->
        refreshTopLine()
    }

    var bottomPre by observable("╰") { _, _, _ ->
        refreshBottomLine()
    }
    var bottomSeq by observable("─") { _, _, _ ->
        refreshBottomLine()
    }
    var bottomTail by observable("╯") { _, _, _ ->
        refreshBottomLine()
    }

    val repeatTimes by observable(120) { _, _, _ ->
        refreshBottomLine()
        refreshTopLine()
    }

    private fun refreshTopLine() {
        topLine = topPre + (topSeq * repeatTimes) + topTail
    }

    private fun refreshBottomLine() {
        bottomLine = bottomPre + (bottomSeq * repeatTimes) + bottomTail
    }

    private var topLine = topPre + (topSeq * repeatTimes) + topTail
    private var bottomLine = bottomPre + (bottomSeq * repeatTimes) + bottomTail
}

typealias LogListener = (Int, String) -> Unit

fun Throwable.log() = ("[Throwable]\n" + Log.getStackTraceString(this)).loge(1)

fun Any?.logv(stackOffset: Int = 1) = Logger.logv(stackOffset) { this }
fun Any?.logd(stackOffset: Int = 1) = Logger.logd(stackOffset) { this }
fun Any?.logi(stackOffset: Int = 1) = Logger.logi(stackOffset) { this }
fun Any?.logw(stackOffset: Int = 1) = Logger.logw(stackOffset) { this }
fun Any?.loge(stackOffset: Int = 1) = Logger.loge(stackOffset) { this }
