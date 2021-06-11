package com.github.ielse.imagewatcher.viewprovider

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View

abstract class LoadingUIProvider {

    internal abstract fun initialView(context: Context): View

    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    var runDelayDisplay: Runnable? = null

    internal fun start(loadView: View) {
        runDelayDisplay?.also { mHandler.removeCallbacks(it) }
        runDelayDisplay = Runnable {
            loadView.visibility = View.VISIBLE
        }.also {
            mHandler.postDelayed(it, 500)
        }
    }

    internal fun stop(loadView: View) {
        runDelayDisplay?.also { mHandler.removeCallbacks(it) }
        loadView.visibility = View.GONE
    }
}