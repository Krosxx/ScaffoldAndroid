@file:Suppress("MemberVisibilityCanBePrivate", "unused", "UNUSED_PARAMETER")

package cn.daqinjia.android.common.ext

import android.content.res.Resources

/**
 * # DensityExt
 * Created on 2019/11/26
 *
 * @author Vove
 */


/**
 * px dp sp 单位相互转换
 * ```kotlin
 * var p = 16.dp.px  //16dp -> px
 *
 * //NoClassDefFoundError ???
 * assertEquals(p == 16.dp to PX)
 * ```
 */


internal val density: Float get() = Resources.getSystem().displayMetrics.density
internal val scaledDensity: Float get() = Resources.getSystem().displayMetrics.scaledDensity

val Int.dp get() = Dp(this.toFloat())
val Float.dp get() = Dp(this)
val Int.px get() = Px(this)
val Number.sp get() = Sp(this.toFloat())

//object PX
//object SP
//object DP

class Dp(val value: Float) {
    val px: Int = dp2px(value).toInt()
    val pxf: Float = dp2px(value)

    private fun dp2px(dpValue: Float): Float {
        return (0.5f + dpValue * density)
    }
}

class Px(val value: Int) {
    val dp: Int get() = px2dp(value).toInt()
    val sp: Float get() = px2sp(value).toFloat()

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private fun px2dp(pxValue: Int): Float {
        return pxValue / density
    }

    private fun px2sp(pxValue: Int): Int {
        val fontScale = scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }
}

class Sp(val value: Float) {
    val px: Int = sp2px(value)

    private fun sp2px(spValue: Float): Int {
        val fontScale = scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}

