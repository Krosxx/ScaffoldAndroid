package com.github.ielse.imagewatcher.viewprovider

import android.content.Context
import android.net.Uri
import android.view.View
import com.github.ielse.imagewatcher.ImageWatcher

interface IndexProvider {
    fun initialView(context: Context): View

    fun onPageChanged(imageWatcher: ImageWatcher, position: Int, dataList: List<Uri>)
}