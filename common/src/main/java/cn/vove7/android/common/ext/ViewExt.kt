package cn.vove7.android.common.ext

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

/**
 * # ViewExt
 * Created on 2019/11/26
 *
 * @author Vove
 */

fun View.gone() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

var View.isShow: Boolean
    get() {
        return isShown
    }
    set(value) {
        if (value) show()
        else gone()
    }

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.fadeIn(duration: Long = 300) {
    if (isShown) {
        return
    }
    show()
    startAnimation(AlphaAnimation(0f, 1f).apply {
        setDuration(duration)
    })
}


fun View.fadeOut(duration: Long = 300) {
    if (visibility != View.VISIBLE) {
        return
    }
    startAnimation(AlphaAnimation(1f, 0f).apply {
        setDuration(duration)
        this.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                gone()
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    })
}

