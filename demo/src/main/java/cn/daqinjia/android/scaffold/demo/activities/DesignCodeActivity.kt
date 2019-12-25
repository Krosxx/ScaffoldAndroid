package cn.daqinjia.android.scaffold.demo.activities

import android.graphics.Color
import android.view.View
import android.widget.TextView
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.common.ext.asColor
import cn.daqinjia.android.common.ext.span
import cn.daqinjia.android.common.ext.spanColor
import cn.daqinjia.android.scaffold.ui.base.NoBindingActivity
import cn.daqinjia.ui_design.MessageDialog
import cn.daqinjia.ui_design.Toast.toastSimple
import cn.daqinjia.ui_design.Toast.toastSuccess
import cn.daqinjia.ui_design.Toast.toastWarning
import kotlinx.android.synthetic.main.activity_design_code_demo.*

/**
 * # DesignCodeActivity
 *
 * Created on 2019/12/23
 * @author Vove
 */
class DesignCodeActivity : NoBindingActivity() {
    override val layoutRes: Int
        get() = R.layout.activity_design_code_demo

    fun toastSuccess(view: View) {
        toastSuccess("修改成功")
    }

    fun toastWarning(view: View) {
        toastWarning((view as TextView).text)
    }

    fun toastSimple(view: View) {
        toastSimple((view as TextView).text)
    }

    fun normalDialog(view: View) {
        MessageDialog.show(this) {
            setTitle("标题")
            content = "告知当前状态，信息和操作方法"
            leftButton("取消".spanColor("#6B6B6B"))
            rightButton("主操作".spanColor("#282828")) {
                toastSimple("Success")
            }
        }
    }

    fun customDialog(view: View) {
        MessageDialog.show(this) {
            setTitle(this@DesignCodeActivity.dialog_title.text)
            content = this@DesignCodeActivity.dialog_content.text
            autoDismiss = this@DesignCodeActivity.dialog_auto_dismiss.isChecked
            leftButton(this@DesignCodeActivity.dialog_left_text.text)
            rightButton(this@DesignCodeActivity.dialog_right_text.text)
        }
    }

    fun onlyContentDialog(view: View) {
        MessageDialog.show(this) {
            setCancelable(false)
            setTitle(" ".span(fontSize = 10))
            content = "是否确认注销大亲家app".span(fontSize = 18, color = Color.BLACK)
            leftButton("确认注销".span(fontSize = 17, color = Color.BLACK)) {
            }
            rightButton("我再想想".span(fontSize = 17, color = "#D93830".asColor))
        }
    }

}