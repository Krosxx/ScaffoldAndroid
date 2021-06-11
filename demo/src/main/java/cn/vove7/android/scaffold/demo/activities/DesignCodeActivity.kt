package cn.vove7.android.scaffold.demo.activities

import android.graphics.Color
import android.view.View
import android.widget.TextView
import cn.vove7.android.scaffold.demo.R
import cn.vove7.android.common.ext.asColor
import cn.vove7.android.common.ext.span
import cn.vove7.android.common.ext.spanColor
import cn.vove7.android.scaffold.demo.databinding.ActivityDesignCodeDemoBinding
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import cn.vove7.ui_design.MessageDialog
import cn.vove7.ui_design.Toast.toastSimple
import cn.vove7.ui_design.Toast.toastSuccess
import cn.vove7.ui_design.Toast.toastWarning

/**
 * # DesignCodeActivity
 *
 * Created on 2019/12/23
 * @author Vove
 */
class DesignCodeActivity : ScaffoldActivity<ActivityDesignCodeDemoBinding>() {

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
            setTitle(binding.dialogTitle.text)
            content = binding.dialogContent.text
            autoDismiss = binding.dialogAutoDismiss.isChecked
            leftButton(binding.dialogLeftText.text)
            rightButton(binding.dialogRightText.text)
        }
    }

    fun onlyContentDialog(view: View) {
        MessageDialog.show(this) {
            setCancelable(false)
            setTitle(" ".span(fontSize = 10))
            content = "是否确认注销app".span(fontSize = 18, color = Color.BLACK)
            leftButton("确认注销".span(fontSize = 17, color = Color.BLACK)) {
            }
            rightButton("我再想想".span(fontSize = 17, color = "#D93830".asColor))
        }
    }

}