package cn.daqinjia.android.scaffold.demo.activities

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ScrollView
import cn.daqinjia.android.common.*
import cn.daqinjia.android.common.Logger.logd
import cn.daqinjia.android.common.Logger.loge
import cn.daqinjia.android.common.Logger.logi
import cn.daqinjia.android.common.Logger.logv
import cn.daqinjia.android.common.Logger.logw
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.databinding.ActivityLogBinding
import cn.daqinjia.android.scaffold.ext.spanColor
import cn.daqinjia.android.scaffold.ui.base.ScaffoldActivity
import kotlinx.android.synthetic.main.activity_log.*

/**
 * # LogActivity
 *
 * Created on 2019/12/19
 * @author Vove
 */
class LogActivity : ScaffoldActivity<ActivityLogBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_log

    private val lcMap = mapOf(
        Log.VERBOSE to Color.BLACK,
        Log.DEBUG to Color.MAGENTA,
        Log.ERROR to Color.RED,
        Log.INFO to Color.GREEN,
        Log.WARN to Color.CYAN
    )

    private val logListener: LogListener = { l, m ->
        runOnUiThread {
            log_text.append(
                (m + "\n").spanColor(lcMap[l] ?: error("unsupport level $l"))
            )
            log_text.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    override fun initView() {
        scrollView.isSmoothScrollingEnabled = true
        Logger.listen(logListener)
        logd { "enter" }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.removeListener(logListener)
    }

    fun log(view: View) = try {

        view.logv()
        view.logd()
        view.logi()
        view.loge()

        logv { "VVVVVVVVVVVV" }
        logd { "DDDDDDDDDDDD" }
        logi { "IIIIIIIIIIII" }
        logw { "WWWWWWWWWWWW" }
        loge { "EEEEEEEEEEEE" }

        1 / 0

    } catch (e: Throwable) {
        e.log()
    }
}