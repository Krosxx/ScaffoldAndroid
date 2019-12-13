package cn.daqinjia.android.scaffold.ui.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cn.daqinjia.android.scaffold.ext.fadeIn
import cn.daqinjia.android.scaffold.ext.fadeOut
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    if (isGone) {
        view.fadeOut()
    } else {
        view.fadeIn()
    }
}

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
