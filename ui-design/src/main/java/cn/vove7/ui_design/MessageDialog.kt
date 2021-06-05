package cn.vove7.ui_design

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import cn.vove7.android.common.ext.gone
import cn.vove7.android.common.ext.show
import cn.vove7.ui_design.databinding.DesignCodeMessageDialogBinding

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

    private val binding by lazy {
        DesignCodeMessageDialogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window?.attributes?.title.also {
            if (it.isNullOrEmpty()) {
                binding.dialogTitle.gone()
            } else {
                binding.dialogTitle.show()
                binding.dialogTitle.text = it
            }
        }
        if (content.isNullOrEmpty()) {
            binding.dialogMessage.gone()
        } else {
            binding.dialogMessage.show()
            binding.dialogMessage.text = content
        }

        arrayOf(leftButton to binding.dialogLeftTxt, rightButton to binding.dialogRightTxt).forEach {
            val buttonData = it.first
            val button = it.second

            if (buttonData == null || buttonData.first.isNullOrEmpty()) {
                button.gone()
                binding.buttonLine.gone()
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