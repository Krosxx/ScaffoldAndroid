package cn.vove7.android.scaffold.demo.activities

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ScrollView
import cn.vove7.android.common.*
import cn.vove7.android.common.Logger.logd
import cn.vove7.android.common.Logger.loge
import cn.vove7.android.common.Logger.logi
import cn.vove7.android.common.Logger.logv
import cn.vove7.android.common.Logger.logw
import cn.vove7.android.scaffold.demo.databinding.ActivityLogBinding
import cn.vove7.android.common.ext.spanColor
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity

/**
 * # LogActivity
 *
 * Created on 2019/12/19
 * @author Vove
 */
class LogActivity : ScaffoldActivity<ActivityLogBinding>() {

    private val lcMap = mapOf(
        Log.VERBOSE to Color.BLACK,
        Log.DEBUG to Color.MAGENTA,
        Log.ERROR to Color.RED,
        Log.INFO to Color.GREEN,
        Log.WARN to Color.CYAN
    )

    private val logListener: LogListener = { l, m ->
        runOnUiThread {
            binding.logText.append(
                (m + "\n").spanColor(lcMap[l] ?: error("unsupport level $l"))
            )
            binding.logText.post {
                binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    override fun initView() {
        binding.scrollView.isSmoothScrollingEnabled = true
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