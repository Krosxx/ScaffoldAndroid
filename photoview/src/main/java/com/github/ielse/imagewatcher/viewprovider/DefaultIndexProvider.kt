package com.github.ielse.imagewatcher.viewprovider

import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.github.ielse.imagewatcher.ImageWatcher

class DefaultIndexProvider : IndexProvider {
    private lateinit var tCurrentIdx: TextView

    override fun initialView(context: Context): View {
        tCurrentIdx = TextView(context)

        val lpCurrentIdx = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lpCurrentIdx.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        tCurrentIdx.layoutParams = lpCurrentIdx
        tCurrentIdx.setTextColor(-0x1)
        val displayMetrics = context.resources.displayMetrics
        val tCurrentIdxTransY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -30f, displayMetrics) + 0.5f
        tCurrentIdx.translationY = tCurrentIdxTransY
        return tCurrentIdx
    }

    override fun onPageChanged(imageWatcher: ImageWatcher, position: Int, dataList: List<Uri>) {
        if (dataList.size > 1) {
            tCurrentIdx.visibility = View.VISIBLE
            val idxInfo = (position + 1).toString() + " / " + dataList.size
            tCurrentIdx.text = idxInfo
        } else {
            tCurrentIdx.visibility = View.GONE
        }
    }
}