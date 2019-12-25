package cn.daqinjia.android.scaffold.ui.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cn.daqinjia.android.common.ext.fadeIn
import cn.daqinjia.android.common.ext.fadeOut
import cn.daqinjia.android.common.ext.gone
import cn.daqinjia.android.common.ext.show
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener

@BindingAdapter(
    "isGone",
    "goneAni",
    requireAll = false
)
fun bindIsGone(
    view: View,
    isGone: Boolean,
    goneAni: Boolean = false
) {
    when (isGone * 2 + goneAni) {
        //f,f
        0 -> view.show()
        //f,t
        1 -> view.fadeIn()
        //t,f
        2 -> view.gone()
        //t,t
        3 -> view.fadeOut()
    }
}

private fun Boolean.toInt(): Int = if (this) 1 else 0

private operator fun Int.plus(b: Boolean): Int = b.toInt() + this

private operator fun Boolean.plus(b: Int): Int = this.toInt() + b

private operator fun Boolean.times(i: Int): Int = i * this.toInt()


/**
 * ImageView 属性适配
 *
 * @param glimpse 若使用，需在 app module 引入 [glimpse-android](https://github.com/the-super-toys/glimpse-android)
 */
@BindingAdapter(
    "imageUrl",
    "imagePlaceholder",
    "circleCropImage",
    "crossFadeImage",
    "overrideImageWidth",
    "overrideImageHeight",
    "glimpse",
    "imageLoadListener",
    requireAll = false
)
fun bindImage(
    imageView: ImageView,
    imageUrl: String?,
    placeholder: Int? = null,
    circleCrop: Boolean? = false,
    crossFade: Boolean? = false,
    overrideWidth: Int? = null,
    overrideHeight: Int? = null,
    glimpse: Boolean = false,
    listener: RequestListener<Drawable>?
) {
    if (imageUrl == null) return
    Glide.with(imageView.context).load(imageUrl).apply {
        if (placeholder != null) {
            placeholder(placeholder)
        }
        if (circleCrop == true) {
            circleCrop()
        }
        if (crossFade == true) {
            transition(DrawableTransitionOptions.withCrossFade())
        }
        if (overrideWidth != null && overrideHeight != null) {
            override(overrideWidth, overrideHeight)
        }
        if (glimpse) {
            //缓存
            diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            transform(Class.forName("glimpse.glide.GlimpseTransformation").newInstance() as BitmapTransformation)
        }
        if (listener != null) {
            listener(listener)
        }
    }.into(imageView)
}
