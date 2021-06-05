package com.github.ielse.imagewatcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.ielse.imagewatcher.databinding.ActivityPhotoViewBinding
import com.github.ielse.imagewatcher.viewprovider.DefaultLoadingUIProvider
import java.util.*


/**
 * 图片浏览
 * @param fromView ImageView?
 * @param url String 当前图片
 * @param urls List<String> 图片数据Uri格式
 * @param provider [ViewExtProvider]?
 */
fun PhotoViewActivity.Companion.start(
    context: Context,
    fromView: ImageView?,
    url: String,
    urls: List<String>,
    provider: ViewExtProvider? = null
) {
    start(context, fromView, urls, urls.indexOf(url), provider)
}

fun PhotoViewActivity.Companion.start(
    context: Context,
    fromView: ImageView?,
    url: String,
    provider: ViewExtProvider? = null
) {
    start(context, fromView, url, listOf(url), provider)
}

/**
 * 图片浏览
 * @param context Context
 * @param fromView ImageView
 * @param initPosition Int
 * @param provider ViewExtProvider?
 */
fun PhotoViewActivity.Companion.start(
    context: Context,
    fromView: ImageView?,
    urls: List<String>,
    initPosition: Int,
    provider: ViewExtProvider? = null
) {
    val data = ViewData(fromView, initPosition, urls.toUriList(), provider)

    val tag = newTag
    putData(tag, data)

    getIntent(context).also {
        it.putExtra("tag", tag)
        ActivityCompat.startActivity(context, it, null)
    }
}


/**
 * # PhotoViewActivity
 * 图片浏览
 * 支持拖拽
 * ```kotlin
 * PhotoViewActivity.start(context, clickedImageView, clickedUrl, imageUrls)
 * ```
 * @author Vove
 * 2019/7/9
 */
class PhotoViewActivity : ScaffoldActivity<ActivityPhotoViewBinding>(),
    ImageWatcher.OnStateChangedListener {

    companion object {
        internal val newTag: Int
            get() {
                var tag = 0
                val random by lazy { Random() }
                while (cacheArray?.get(tag) != null) {
                    tag = random.nextInt(50) + 50
                }
                return tag
            }

        private var cacheArray: SparseArray<ViewData>? = null

        internal fun putData(tag: Int, data: ViewData) =
            synchronized(PhotoViewActivity::class.java) {
                if (cacheArray == null) {
                    cacheArray = SparseArray()
                }
                cacheArray?.put(tag, data)
            }

        internal fun getIntent(context: Context): Intent {
            /**
             * 防止在全屏浏览图片时，前Activity布局上移
             * 若context !is Activity 可手动加入
             * 同时在activity根布局
             * android:fitsSystemWindows="true"
             */
            if (context is Activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    context.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE /*or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN*/
                } else {//

                }
            }

            return Intent(context, PhotoViewActivity::class.java)
        }

    }

    private lateinit var viewData: ViewData

    var tag = 0


    override fun onBackPressed() {
        if (!findViewById<ImageWatcher>(R.id.image_watcher) .handleBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        onInitialization()
    }


    private fun onInitialization() {
        overridePendingTransition(0, 0)

        cacheArray?.get(tag)?.also {
            viewData = it
        }
        if (!::viewData.isInitialized) {
            finish()
            return
        }

        initWatcher()
        initContainer()
        viewData.provider?.apply {
            attachWatcher(binding.imageWatcher)
            attachActivity(this@PhotoViewActivity)
        }

        viewData.apply {
            if (fromView != null) {
                binding.imageWatcher.show(fromView, rawImgUrls, initPostion)
            } else {
                binding.imageWatcher.show(rawImgUrls ?: emptyList(), initPostion)
            }
        }
    }

    private fun initContainer() {
        viewData.provider?.extLayout?.also {
            binding.outContainer.addView(layoutInflater.inflate(it, null))
            viewData.provider?.onInitView(binding.outContainer)
        }
    }

    private fun initWatcher() {
        binding.imageWatcher.apply {
            setLoader(GlideSimpleLoader())
            setLoadingUIProvider(DefaultLoadingUIProvider())
            setOnPictureLongPressListener { v, uri, pos ->
                viewData.provider?.onLongClick(v, uri, pos)
            }
            viewData.provider?.also {
                addOnPageChangeListener(it)
            }
            addOnStateChangedListener(this@PhotoViewActivity)
        }
    }

    override fun onStateChangeUpdate(
        imageWatcher: ImageWatcher,
        clicked: ImageView?,
        position: Int,
        uri: Uri?,
        animatedValue: Float,
        actionTag: Int
    ) {

    }

    override fun onStateChanged(
        imageWatcher: ImageWatcher,
        position: Int,
        uri: Uri?,
        actionTag: Int
    ) {
        when (actionTag) {
            ImageWatcher.STATE_EXIT_HIDING -> finish()
            ImageWatcher.STATE_ENTER_DISPLAYING -> binding.outContainer.visibility = View.VISIBLE
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }


    private fun release() = synchronized(PhotoViewActivity::class.java) {
        cacheArray?.remove(tag)
        if (cacheArray?.size() == 0) {
            cacheArray = null
        }
    }

}

private fun <E> Collection<E>.toSArray(): SparseArray<E> {
    return SparseArray<E>().also {
        forEachIndexed { index, e ->
            it.put(index, e)
        }
    }
}

private operator fun <E> SparseArray<E>.set(tag: Int, value: E) {
    put(tag, value)
}

/**
 * PhotoViewActivity传递参数
 * @property fromView ImageView?
 * @property imageViews List<ImageView>?
 * @property rawImgUrls List<Uri>?
 * @property initPostion Int
 * @property provider ViewExtProvider?
 */
internal class ViewData {
    var fromView: ImageView? = null
    //    var imageViews: List<ImageView>? = null
    var rawImgUrls: List<Uri>? = null
    var initPostion: Int = 0

    var provider: ViewExtProvider? = null

    constructor(
        fromView: ImageView?,
        initPosition: Int,
        rawImgUrls: List<Uri>?,
        provider: ViewExtProvider?
    ) {
        this.fromView = fromView
        this.initPostion = initPosition
        this.rawImgUrls = rawImgUrls
        this.provider = provider
    }
}

/**
 * 用于扩展视图操作，例如扩展[下载/查看原图]功能
 * 实现ViewPager.OnPageChangeListener 可选动态更新视图
 * --------------------
 * |            {分享} |
 * |                  |
 * |                  |
 * |       图片        |
 * |                  |
 * |                  |
 * |                  |
 * |                  |
 * |                  |
 * |      {下载}       |
 * --------------------
 *
 * @property extLayout Int?
 * @property watcher ImageWatcher
 * @property currentUri Uri?
 * @property currentPosition Int
 */
abstract class ViewExtProvider : ViewPager.OnPageChangeListener {
    open fun onLongClick(view: ImageView, uri: Uri, pos: Int) {}

    //扩展视图资源
    open val extLayout: Int? = null

    lateinit var watcher: ImageWatcher

    /**
     * 获取当前Activity
     */
    lateinit var activity: Activity

    fun attachWatcher(watcher: ImageWatcher) {
        this.watcher = watcher
    }

    fun attachActivity(activity: Activity) {
        this.activity = activity
    }

    fun exitView() {
        activity.onBackPressed()
    }

    /**
     * 当前图片Uri
     */
    val currentUri: Uri? get() = watcher.getUri(currentPosition)

    /**
     * 当前图片位置
     */
    val currentPosition: Int get() = watcher.currentPosition

    /**
     * 初始化布局View
     * @param container ViewGroup
     */
    abstract fun onInitView(container: ViewGroup)

    //不用子类强制实现
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
    }
}

fun List<String>.toUriList(): List<Uri> {
    return map { Uri.parse(it) }
}

internal class GlideSimpleLoader : ImageWatcher.Loader {
    override fun load(context: Context, uri: Uri, lc: ImageWatcher.LoadCallback) {
        Glide.with(context)
            .asDrawable()
            .load(uri)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    lc.onResourceReady(resource)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    lc.onLoadStarted(placeholder)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    lc.onLoadFailed(errorDrawable)
                }
            })
    }
}
