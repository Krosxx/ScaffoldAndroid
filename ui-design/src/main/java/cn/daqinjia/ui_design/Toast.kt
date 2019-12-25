package cn.daqinjia.ui_design

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.StringRes
import cn.daqinjia.android.common.ext.gone
import cn.daqinjia.android.common.ext.show
import cn.daqinjia.ui_design.ext.APPLICATION_INSTANCE
import cn.daqinjia.ui_design.ext.runOnUi
import kotlinx.android.synthetic.main.design_code_toast.view.*

/**
 * 设计规范 -  Toast
 */
object Toast {

    const val LENGTH_SHORT = 0
    const val LENGTH_LONG = 1

    private val context
        get() = APPLICATION_INSTANCE

    private fun showToast(icon: Int?, content: CharSequence, duration: Int) = runOnUi {
        val toast = Toast(context)
        val customView = LayoutInflater.from(context).inflate(R.layout.design_code_toast, null)

        val contentView = customView.toast_content
        contentView.text = content

        val iconView = customView.toast_icon
        if (icon == null) {
            iconView.gone()
        } else {
            iconView.show()
            iconView.setImageResource(icon)
        }

        toast.view = customView
        toast.duration = duration
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    @JvmStatic
    @JvmOverloads
    fun toastSimple(@StringRes sId: Int, duration: Int = Toast.LENGTH_SHORT) {
        toastSimple(context.getString(sId), duration)
    }

    @JvmStatic
    @JvmOverloads
    fun toastSimple(content: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        showToast(null, content, duration)
    }

    @JvmStatic
    @JvmOverloads
    fun toastWarning(content: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        showToast(R.drawable.warning_o, content, duration)
    }

    @JvmStatic
    @JvmOverloads
    fun toastSuccess(content: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        showToast(R.drawable.toast_success, content, duration)
    }

}