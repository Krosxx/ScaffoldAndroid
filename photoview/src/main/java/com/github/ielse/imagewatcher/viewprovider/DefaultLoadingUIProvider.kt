package com.github.ielse.imagewatcher.viewprovider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout.LayoutParams
import android.widget.ProgressBar

class DefaultLoadingUIProvider : LoadingUIProvider() {
    private val lpCenterInParent = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

    override fun initialView(context: Context): View {
        lpCenterInParent.gravity = Gravity.CENTER
        val progressView = ProgressBar(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressView.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
        }
        progressView.layoutParams = lpCenterInParent
        return progressView
    }

}