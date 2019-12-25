package cn.daqinjia.ui_design

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import cn.daqinjia.android.common.ext.gone
import cn.daqinjia.android.common.ext.show
import kotlinx.android.synthetic.main.design_code_message_dialog.*

typealias OnClick = Function1<MessageDialog, Unit>


class MessageDialog(context: Context) : Dialog(context) {

    companion object {
        @JvmStatic
        fun show(context: Context, builderAction: MessageDialog.() -> Unit): MessageDialog {
            return MessageDialog(context).apply(builderAction).also { it.show() }
        }
    }

    var content: CharSequence? = null
    var autoDismiss = true

    private var leftButton: Pair<CharSequence, OnClick?>? = null
    private var rightButton: Pair<CharSequence, OnClick?>? = null

    fun leftButton(text: CharSequence, click: OnClick? = null) {
        leftButton = text to click
    }

    fun rightButton(text: CharSequence, click: OnClick? = null) {
        rightButton = text to click
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.design_code_message_dialog)

        window?.attributes?.title.also {
            if (it.isNullOrEmpty()) {
                dialog_title.gone()
            } else {
                dialog_title.show()
                dialog_title.text = it
            }
        }
        if (content.isNullOrEmpty()) {
            dialog_message.gone()
        } else {
            dialog_message.show()
            dialog_message.text = content
        }

        arrayOf(leftButton to dialog_left_txt, rightButton to dialog_right_txt).forEach {
            val buttonData = it.first
            val button = it.second

            if (buttonData == null || buttonData.first.isNullOrEmpty()) {
                button.gone()
                button_line.gone()
            } else {
                button.text = buttonData.first
                button.setOnClickListener {
                    buttonData.second?.invoke(this)
                    if (autoDismiss) {
                        dismiss()
                    }
                }
            }

        }
    }

    override fun show() {
        super.show()
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}