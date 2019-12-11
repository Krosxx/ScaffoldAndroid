package com.github.ielse.imagewatcher

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.ielse.imagewatcher.viewprovider.DefaultIndexProvider
import com.github.ielse.imagewatcher.viewprovider.DefaultLoadingUIProvider
import com.github.ielse.imagewatcher.viewprovider.IndexProvider
import com.github.ielse.imagewatcher.viewprovider.LoadingUIProvider
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.abs

/**
 * 修改于com.github.ielse.imagewatcher.ImageWatcher
 */
class ImageWatcher @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), GestureDetector.OnGestureListener, ViewPager.OnPageChangeListener {
    private val mHandler: Handler
    //当前展示ImageView
    private var iSource: ImageView? = null

    protected var mErrorImageRes = R.drawable.error_picture // 图片加载失败站位图
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mBackgroundColor = 0x00000000
    private var mTouchMode = TOUCH_MODE_NONE
    private val mTouchSlop: Float

    private var mFingersDistance: Float = 0.toFloat()
    private var mFingersCenterX: Float = 0.toFloat()
    private var mFingersCenterY: Float = 0.toFloat()
    private var mExitRef: Float = 0.toFloat() // 触摸退出进度

    private var animFling: ValueAnimator? = null
    private var animBackground: ValueAnimator? = null
    private var animImageTransform: ValueAnimator? = null
    private var isInTransformAnimation: Boolean = false
    private val mGestureDetector: GestureDetector

    private var isInitLayout = false
    protected var initI: ImageView? = null // 显示ImageWatcher时点击view
    protected var initImageGroupList: SparseArray<ImageView>? = null // imageView控件映射列表
    protected var initUrlList: MutableList<Uri>? = null

    private var pictureLongPressListener: OnPictureLongPressListener? = null // 图片长按回调
    private lateinit var adapter: ImagePagerAdapter
    private var vPager: ViewPager
    protected var mImageGroupList: SparseArray<ImageView>? =
        null // 图片所在的ImageView控件集合，Int类型的Key对应position
    protected var mUrlList: MutableList<Uri> = mutableListOf() // 图片地址列表
    protected var initPosition: Int = 0
    var currentPosition: Int = 0
        private set
    private var mPagerPositionOffsetPixels: Int = 0 // viewpager当前在屏幕上偏移量
    private var loader: Loader? = null // 图片加载者
    private val onStateChangedListeners = ArrayList<OnStateChangedListener>()
    private var indexProvider: IndexProvider? = null // 索引ui接口
    private var idxView: View? = null // 索引ui
    private var loadingUIProvider: LoadingUIProvider? = null // 加载ui
    private val onPageChangeListeners = ArrayList<ViewPager.OnPageChangeListener>()

    private var detachAffirmative: Boolean = false // dismiss detach parent 退出查看大图模式后，立即释放内存
    private var detachedParent: Boolean = false

    //下拉退出系数，越大角度越小（接近垂直才可退出），1为45度
    private val dragExitCoefficient = 1

    val displayingUri: Uri?
        get() = getUri(currentPosition)

    val screenWidth: Int
        get() = context.resources.displayMetrics.widthPixels

    val screenHeight: Int
        get() = context.resources.displayMetrics.heightPixels


    /**
     * 动画执行时加入这个监听器后会自动记录标记 [ImageWatcher.isInTransformAnimation] 的状态<br></br>
     * isInTransformAnimation值为true的时候可以达到在动画执行时屏蔽触摸操作的目的
     */
    private val mAnimTransitionStateListener = object : AnimatorListenerAdapter() {
        override fun onAnimationCancel(animation: Animator) {
            isInTransformAnimation = false
        }

        override fun onAnimationStart(animation: Animator) {
            isInTransformAnimation = true
            mTouchMode = TOUCH_MODE_AUTO_FLING
        }

        override fun onAnimationEnd(animation: Animator) {
            isInTransformAnimation = false
        }
    }

    private val mColorEvaluator = TypeEvaluator<Int> { fraction, startValue, endValue ->
        val f = accelerateInterpolator.getInterpolation(fraction)
        val startColor = startValue!!
        val endColor = endValue!!

        val alpha =
            (Color.alpha(startColor) + f * (Color.alpha(endColor) - Color.alpha(startColor))).toInt()
        val red =
            (Color.red(startColor) + f * (Color.red(endColor) - Color.red(startColor))).toInt()
        val green =
            (Color.green(startColor) + f * (Color.green(endColor) - Color.green(startColor))).toInt()
        val blue =
            (Color.blue(startColor) + f * (Color.blue(endColor) - Color.blue(startColor))).toInt()
        Color.argb(alpha, red, green, blue)
    }

    private val decelerateInterpolator = DecelerateInterpolator()
    private val accelerateInterpolator = AccelerateInterpolator()

    init {
        mHandler = RefHandler(this)
        mGestureDetector = GestureDetector(context, this)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        addView(ViewPager(context).also { vPager = it })
        vPager.addOnPageChangeListener(this)
        visibility = View.INVISIBLE
        setIndexProvider(DefaultIndexProvider())
        setLoadingUIProvider(DefaultLoadingUIProvider())
    }

    fun setLoader(l: Loader) {
        loader = l
    }

    fun setDetachAffirmative() {
        this.detachAffirmative = true
    }

    fun setIndexProvider(ip: IndexProvider) {
        indexProvider = ip
        if (indexProvider != null) {
            if (idxView != null) removeView(idxView)
            idxView = indexProvider!!.initialView(context)
            addView(idxView)
        }
    }

    fun setLoadingUIProvider(lp: LoadingUIProvider) {
        loadingUIProvider = lp
    }

    fun addOnStateChangedListener(listener: OnStateChangedListener) {
        if (!onStateChangedListeners.contains(listener)) {
            onStateChangedListeners.add(listener)
        }
    }

    fun setOnPictureLongPressListener(listener: OnPictureLongPressListener) {
        pictureLongPressListener = listener
    }

    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        if (!onPageChangeListeners.contains(listener)) {
            onPageChangeListeners.add(listener)
        }
    }

    interface Loader {
        fun load(context: Context, uri: Uri, lc: LoadCallback)
    }

    interface LoadCallback {
        fun onResourceReady(resource: Drawable)

        fun onLoadStarted(placeholder: Drawable?)

        fun onLoadFailed(errorDrawable: Drawable?)
    }


    interface OnStateChangedListener {
        fun onStateChangeUpdate(
            imageWatcher: ImageWatcher,
            clicked: ImageView?,
            position: Int,
            uri: Uri?,
            animatedValue: Float,
            actionTag: Int
        )

        fun onStateChanged(imageWatcher: ImageWatcher, position: Int, uri: Uri?, actionTag: Int)
    }

    fun show(urlList: List<Uri>, initPos: Int): Boolean {
        return show(null, urlList, initPos)
    }

    fun show(i: ImageView?, urlList: List<Uri>?, initPos: Int): Boolean {
        if (urlList == null) {
            Log.e("ImageWatcher", "urlList[null]")
            return false
        }
        if (initPos >= urlList.size || initPos < 0) {
            Log.e("ImageWatcher", "initPos[" + initPos + "]  urlList.size[" + urlList.size + "]")
            return false
        }
        initPosition = initPos
        val a = SparseArray<ImageView>()
        a.put(initPosition, i)
        showInternal(i, a, urlList)
        return true
    }

    /**
     * 调用show方法前，请先调用setLoader 给ImageWatcher提供加载图片的实现
     *
     * @param i              被点击的ImageView
     * @param imageGroupList 被点击的ImageView的所在列表，加载图片时会提前展示列表中已经下载完成的thumb图片
     * @param urlList        被加载的图片url列表，数量必须大于等于 imageGroupList.size。 且顺序应当和imageGroupList保持一致
     */
    fun show(
        i: ImageView?,
        imageGroupList: SparseArray<ImageView>?,
        urlList: MutableList<Uri>?
    ): Boolean {
        if (i == null || imageGroupList == null || urlList == null) {
            Log.e("ImageWatcher", "i[$i]  imageGroupList[$imageGroupList]  urlList[$urlList]")
            return false
        }
        initPosition = imageGroupList.keyAt(imageGroupList.indexOfValue(i))
        if (initPosition < 0) {
            Log.e(
                "ImageWatcher",
                "param ImageView i must be a member of the List <ImageView> imageGroupList!"
            )
            return false
        }

        showInternal(i, imageGroupList, urlList)
        return true
    }

    /**
     * 指定imageGroupList 可在退出时，回到指定view
     *
     * @param position
     * @param imageGroupList
     * @param urlList
     * @return
     */
    fun show(
        position: Int,
        imageGroupList: SparseArray<ImageView>?,
        urlList: MutableList<Uri>?
    ): Boolean {
        if (imageGroupList == null || urlList == null) {
            Log.e("ImageWatcher", "imageGroupList[$imageGroupList]  urlList[$urlList]")
            return false
        }
        initPosition = position
        if (initPosition < 0 || initPosition >= imageGroupList.size()) {
            Log.e("ImageWatcher", "position error $position")
            return false
        }
        val i = imageGroupList.get(position)
        if (i == null) {
            Log.e(
                "ImageWatcher",
                "param ImageView i must be a member of the List <ImageView> imageGroupList!!"
            )
            return false
        }

        showInternal(i, imageGroupList, urlList)
        return true
    }

    private fun showInternal(
        i: ImageView?,
        imageGroupList: SparseArray<ImageView>,
        urlList: List<Uri>
    ) {
        if (loader == null) {
            Log.e("ImageWatcher", "please invoke `setLoader` first [loader == null]")
            return
        }

        initI = i
        if (!isInitLayout) {
            initImageGroupList = imageGroupList
            initUrlList = urlList.toMutableList()
            return
        }

        currentPosition = initPosition

        animImageTransform?.cancel()
        animImageTransform = null

        mImageGroupList = imageGroupList
        mUrlList = urlList.toMutableList()
        iSource = null

        this@ImageWatcher.visibility = View.VISIBLE
        vPager.setAdapter(ImagePagerAdapter().also { adapter = it })
        vPager.currentItem = initPosition
        indexProvider?.onPageChanged(this, initPosition, mUrlList)
    }

    fun getUri(position: Int): Uri? {
        return if (mUrlList.size <= position || position < 0) {
            null
        } else mUrlList[position]
    }

    fun notifyItemChanged(position: Int, newUri: Uri) {
        val uList = mUrlList
        if (mUrlList.size <= position || position < 0) {
            return
        }
        uList[position] = newUri
        adapter.notifyItemChanged(position)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mPagerPositionOffsetPixels == 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (iSource == null) return true

        if (animImageTransform?.isRunning == true) {
            return true
        }

        animFling?.also {
            it.cancel()
            animFling = null
            mTouchMode = TOUCH_MODE_DOWN
        }

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> onUp(event)
            MotionEvent.ACTION_POINTER_DOWN -> if (mPagerPositionOffsetPixels == 0) { // 正在查看一张图片，不处于翻页中。
                if (mTouchMode != TOUCH_MODE_SCALE) {
                    mFingersDistance = 0f
                    mFingersCenterX = 0f
                    mFingersCenterY = 0f
                    ViewState.write(iSource, ViewState.STATE_TOUCH_SCALE)
                }
                mTouchMode = TOUCH_MODE_SCALE // 变化为缩放状态
            } else {
                dispatchEventToViewPager(event)
            }
            MotionEvent.ACTION_POINTER_UP -> if (mPagerPositionOffsetPixels == 0) { // 正在查看一张图片，不处于翻页中。
                if (event.pointerCount - 1 < 1 + 1) {
                    mTouchMode = TOUCH_MODE_SCALE_LOCK
                    onUp(event) // 结束缩放状态
                }
            } else {
                dispatchEventToViewPager(event)
            }
        }
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        mTouchMode = TOUCH_MODE_DOWN
        dispatchEventToViewPager(e)
        return true
    }

    private fun onUp(e: MotionEvent?) {
        // mTouchMode == TOUCH_MODE_AUTO_FLING -> nothing
        if (mTouchMode == TOUCH_MODE_SCALE || mTouchMode == TOUCH_MODE_SCALE_LOCK) {
            handleScaleTouchResult()
        } else if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitTouchResult()
        } else if (mTouchMode == TOUCH_MODE_DRAG) {
            handleDragTouchResult()
        } else if (mTouchMode == TOUCH_MODE_SLIDE) {
            dispatchEventToViewPager(e)
        }
    }

    private fun dispatchEventToViewPager(e2: MotionEvent?, e1: MotionEvent? = null) {
        try {
            if (e2 != null) {
                val moveX = if (e1 != null) (e2.x - e1.x) else 0f
                val moveY = if (e1 != null) e2.y - e1.y else 0f
                if (abs(moveY) > mTouchSlop * 3 && abs(moveX) < mTouchSlop && mPagerPositionOffsetPixels == 0) {
                    ViewState.write(iSource, ViewState.STATE_EXIT)
                    mTouchMode = TOUCH_MODE_EXIT // 下拉返回不灵 mTouchMode SLIDE 变化为 EXIT
                }
            }
            vPager.onTouchEvent(e2)
        } catch (ignore: Exception) {
        }

    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (mTouchMode == TOUCH_MODE_DOWN) {
            val moveX = if (e1 != null) e2.x - e1.x else 0f
            val moveY = if (e1 != null) e2.y - e1.y else 0f
            if (abs(moveX) > mTouchSlop || abs(moveY) > mTouchSlop) {
                val vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT)
                val vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT)
                val imageOrientation = iSource?.orientation ?: 0

                if (vsDefault == null) {
                    // 没有vsDefault标志的View说明图标正在下载中。转化为Slide手势，可以进行viewpager的翻页滑动
                    mTouchMode = TOUCH_MODE_SLIDE
                } else if (abs(moveX) < mTouchSlop && moveY > abs(moveX) * dragExitCoefficient &&
                    vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2 <= iSource!!.translationY
                ) {
                    // 手指下拉[dragExitCoefficient]。 横图未放大or图片放大且显示出了顶端   转化为退出查看图片操作
                    if (mTouchMode != TOUCH_MODE_EXIT) {
                        ViewState.write(iSource, ViewState.STATE_EXIT)
                    }
                    mTouchMode = TOUCH_MODE_EXIT
                } else if (vsCurrent.scaleY > vsDefault.scaleY || vsCurrent.scaleX > vsDefault.scaleX ||
                    vsCurrent.scaleY * iSource!!.height > mHeight
                ) {
                    // 图片当前为放大状态(宽或高超出了屏幕尺寸)or竖图
                    if (mTouchMode != TOUCH_MODE_DRAG) {
                        ViewState.write(iSource, ViewState.STATE_DRAG)
                    }
                    mTouchMode = TOUCH_MODE_DRAG // 转化为Drag手势，可以对图片进行拖拽操作

                    if (LinearLayout.HORIZONTAL == imageOrientation) {
                        // 图片位于边界，且仍然尝试向边界外拽动。。转化为Slide手势，可以进行viewpager的翻页滑动
                        val translateXEdge = vsDefault.width * (vsCurrent.scaleX - 1) / 2
                        if (vsCurrent.translationX >= translateXEdge && moveX > 0) {
                            mTouchMode = TOUCH_MODE_SLIDE
                        } else if (vsCurrent.translationX <= -translateXEdge && moveX < 0) {
                            mTouchMode = TOUCH_MODE_SLIDE
                        }
                    } else if (LinearLayout.VERTICAL == imageOrientation) {
                        if (vsDefault.width * vsCurrent.scaleX <= mWidth) {
                            if (abs(moveY) < mTouchSlop && abs(moveX) > mTouchSlop && abs(moveX) > abs(
                                    moveY
                                ) * 2
                            ) {
                                mTouchMode = TOUCH_MODE_SLIDE
                            }
                        } else {
                            val translateXRightEdge =
                                vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2
                            val translateXLeftEdge =
                                mWidth.toFloat() - vsDefault.width * vsCurrent.scaleX / 2 - (vsDefault.width / 2).toFloat()
                            if (vsCurrent.translationX >= translateXRightEdge && moveX > 0) {
                                mTouchMode = TOUCH_MODE_SLIDE
                            } else if (vsCurrent.translationX <= translateXLeftEdge && moveX < 0) {
                                mTouchMode = TOUCH_MODE_SLIDE
                            }
                        }
                    }
                } else if (abs(moveX) > mTouchSlop) {
                    mTouchMode = TOUCH_MODE_SLIDE    // 左右滑动。转化为Slide手势，可以进行viewpager的翻页滑动
                }
            }
        }

        when (mTouchMode) {
            TOUCH_MODE_SLIDE -> dispatchEventToViewPager(e2, e1)
            TOUCH_MODE_SCALE -> handleScaleGesture(e2)
            TOUCH_MODE_EXIT -> handleExitGesture(e2, e1)
            TOUCH_MODE_DRAG -> handleDragGesture(e2, e1)
        }
        return false
    }

    /**
     * 处理单击的手指事件
     */
    fun onSingleTapConfirmed(): Boolean {
        if (iSource == null) return false
        val vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT)
        val vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT)
        if (vsDefault == null || vsCurrent.scaleY <= vsDefault.scaleY && vsCurrent.scaleX <= vsDefault.scaleX) {
            mExitRef = 0f
        } else {
            iSource!!.setTag(ViewState.STATE_EXIT, vsDefault)
            mExitRef = 1f
        }
        handleExitTouchResult()
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val hadTapMessage = mHandler.hasMessages(SINGLE_TAP_UP_CONFIRMED)
        if (hadTapMessage) {
            mHandler.removeMessages(SINGLE_TAP_UP_CONFIRMED)
            handleDoubleTapTouchResult()
            return true
        } else {
            mHandler.sendEmptyMessageDelayed(SINGLE_TAP_UP_CONFIRMED, 250)
        }
        return false
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onLongPress(e: MotionEvent) {
        pictureLongPressListener?.invoke(
            iSource!!,
            mUrlList!![vPager.currentItem],
            vPager.currentItem
        )
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        if (iSource != null && mTouchMode != TOUCH_MODE_AUTO_FLING && mPagerPositionOffsetPixels == 0) {
            val vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT)
            val vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT) ?: return false
            val imageOrientation = iSource?.orientation ?: 0

            if (velocityX > 0 && vsCurrent.translationX == vsDefault.width * (vsCurrent.scaleX - 1) / 2 && LinearLayout.VERTICAL == imageOrientation) {
                return false // 当前图片[横图]左侧边缘手指右划
            } else if (velocityX < 0 && -vsCurrent.translationX == vsDefault.width * (vsCurrent.scaleX - 1) / 2 && LinearLayout.HORIZONTAL == imageOrientation) {
                return false // 当前图片[横图]右侧边缘手指左划
            } else if (e1 != null && e2 != null && (abs(e1.x - e2.x) > 50 || abs(e1.y - e2.y) > 50) && (abs(
                    velocityX
                ) > 500 || abs(velocityY) > 500)
            ) {
                // 满足fling手势
                var maxVelocity = Math.max(abs(velocityX), abs(velocityY))
                var endTranslateX = vsCurrent.translationX + velocityX * 0.2f
                var endTranslateY = vsCurrent.translationY + velocityY * 0.2f
                if (vsCurrent.scaleY * iSource!!.height < mHeight) {
                    endTranslateY = vsCurrent.translationY // 当前状态下判定为 横图(所显示高度不过全屏)
                    maxVelocity = abs(velocityX)
                }
                if (vsCurrent.scaleY * iSource!!.height > mHeight && vsCurrent.scaleX == vsDefault.scaleX) {
                    endTranslateX = vsCurrent.translationX // 当前状态下判定为 竖图(所显示宽度不过全屏)
                    maxVelocity = abs(velocityY)
                }

                val overflowX = mWidth * 0.02f
                val translateXEdge = vsDefault.width * (vsCurrent.scaleX - 1) / 2
                if (endTranslateX > translateXEdge + overflowX)
                    endTranslateX = translateXEdge + overflowX
                else if (endTranslateX < -translateXEdge - overflowX)
                    endTranslateX = -translateXEdge - overflowX

                if (vsCurrent.scaleY * iSource!!.height > mHeight) {
                    val overflowY = mHeight * 0.02f
                    val translateYTopEdge =
                        vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2
                    val translateYBottomEdge =
                        mHeight.toFloat() - vsDefault.height * vsCurrent.scaleY / 2 - (vsDefault.height / 2).toFloat()
                    if (endTranslateY > translateYTopEdge + overflowY) {
                        endTranslateY = translateYTopEdge + overflowY
                    } else if (endTranslateY < translateYBottomEdge - overflowY) {
                        endTranslateY = translateYBottomEdge - overflowY
                    }
                }

                animFling(
                    iSource!!,
                    ViewState.write(
                        iSource,
                        ViewState.STATE_TEMP
                    ).translationX(endTranslateX).translationY(endTranslateY),
                    (1000000 / maxVelocity).toLong()
                )
                return true
            }
        }
        return false
    }

    /**
     * 处理响应退出图片查看
     */
    private fun handleExitGesture(e2: MotionEvent, e1: MotionEvent?) {
        if (iSource == null) return
        val vsExit = ViewState.read(iSource, ViewState.STATE_EXIT)
        val vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT)
        if (vsExit == null || vsDefault == null) return

        mExitRef = 1f
        val moveY = e2.y - e1!!.y
        val moveX = e2.x - e1.x
        if (moveY > 0) mExitRef -= moveY / screenHeight
        if (mExitRef < 0) mExitRef = 0f
//        Log.d("Debug :", "mExitRef  ----> $mExitRef")

        setBackgroundColor(mColorEvaluator.evaluate(mExitRef, MIN_BACKGROUND_ALPHA, -0x1000000))
//        val exitScale = MIN_SCALE + (vsExit.scaleX - MIN_SCALE) * mExitRef
        val exitScale = mExitRef * mExitRef
        iSource?.apply {
            scaleX = exitScale
            scaleY = exitScale
            val exitTrans =
                vsDefault.translationX + (vsExit.translationX - vsDefault.translationX) * mExitRef
            translationX = exitTrans + moveX
            translationY = vsExit.translationY + moveY
        }
    }

    /**
     * 处理响应单手拖拽平移
     */
    private fun handleDragGesture(e2: MotionEvent, e1: MotionEvent?) {
        if (iSource == null) return
        val vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT)
        val vsTouchDrag = ViewState.read(iSource, ViewState.STATE_DRAG)
        if (vsDefault == null || vsTouchDrag == null) return
        val moveY = e2.y - e1!!.y
        val moveX = e2.x - e1.x
        var translateXValue = vsTouchDrag.translationX + moveX
        var translateYValue = vsTouchDrag.translationY + moveY

        val imageOrientation = iSource?.orientation ?: 0
        if (LinearLayout.HORIZONTAL == imageOrientation) {
            val translateXEdge = vsDefault.width * (vsTouchDrag.scaleX - 1) / 2
            if (translateXValue > translateXEdge) {
                translateXValue =
                    translateXEdge + (translateXValue - translateXEdge) * EDGE_RESILIENCE
            } else if (translateXValue < -translateXEdge) {
                translateXValue =
                    -translateXEdge + (translateXValue - -translateXEdge) * EDGE_RESILIENCE
            }
            iSource!!.translationX = translateXValue
        } else if (LinearLayout.VERTICAL == imageOrientation) {
            if (vsDefault.width * vsTouchDrag.scaleX <= mWidth) {
                translateXValue = vsTouchDrag.translationX
            } else {
                val translateXRightEdge =
                    vsDefault.width * vsTouchDrag.scaleX / 2 - vsDefault.width / 2
                val translateXLeftEdge =
                    mWidth.toFloat() - vsDefault.width * vsTouchDrag.scaleX / 2 - (vsDefault.width / 2).toFloat()

                if (translateXValue > translateXRightEdge) {
                    translateXValue =
                        translateXRightEdge + (translateXValue - translateXRightEdge) * EDGE_RESILIENCE
                } else if (translateXValue < translateXLeftEdge) {
                    translateXValue =
                        translateXLeftEdge + (translateXValue - translateXLeftEdge) * EDGE_RESILIENCE
                }
            }
            iSource!!.translationX = translateXValue
        }

        if (vsDefault.height * vsTouchDrag.scaleY > mHeight) {
            val translateYTopEdge = vsDefault.height * vsTouchDrag.scaleY / 2 - vsDefault.height / 2
            val translateYBottomEdge =
                mHeight.toFloat() - vsDefault.height * vsTouchDrag.scaleY / 2 - (vsDefault.height / 2).toFloat()
            if (translateYValue > translateYTopEdge) {
                translateYValue =
                    translateYTopEdge + (translateYValue - translateYTopEdge) * EDGE_RESILIENCE
            } else if (translateYValue < translateYBottomEdge) {
                translateYValue =
                    translateYBottomEdge + (translateYValue - translateYBottomEdge) * EDGE_RESILIENCE
            }
            iSource?.apply {
                translationY = translateYValue
                if (proportion > 1f) {//长图 设置pivotY translateYValue(negative)
                    pivotY = (mHeight / 2).toFloat() - translateYValue
                    Log.d("Debug :", "handleDragGesture  ----> $pivotY")
                }
            }
        }
    }

    /**
     * 处理响应双手拖拽缩放
     */
    private fun handleScaleGesture(e2: MotionEvent) {
        val source = iSource ?: return

        val vsCurrent = ViewState.read(source, ViewState.STATE_CURRENT)
        val vsTouchScale = ViewState.read(source, ViewState.STATE_TOUCH_SCALE)
        if (vsCurrent == null || vsTouchScale == null) return

        if (e2.pointerCount < 2) return
        val deltaX = e2.getX(1) - e2.getX(0)
        val deltaY = e2.getY(1) - e2.getY(0)
        //        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        val distance = abs(deltaX) + abs(deltaY)
        if (mFingersDistance == 0f) mFingersDistance = distance
        val changedScale = (mFingersDistance - distance) / (mWidth * SCALE_SENSITIVITY)
        var scaleResultX = vsTouchScale.scaleX - changedScale
        if (scaleResultX < MIN_SCALE)
            scaleResultX = MIN_SCALE
        else if (scaleResultX > MAX_SCALE) scaleResultX = MAX_SCALE
        source.scaleX = scaleResultX
        var scaleResultY = vsTouchScale.scaleY - changedScale
        if (scaleResultY < MIN_SCALE)
            scaleResultY = MIN_SCALE
        else if (scaleResultY > MAX_SCALE) scaleResultY = MAX_SCALE
        source.scaleY = scaleResultY

        val centerX = (e2.getX(1) + e2.getX(0)) / 2
        val centerY = (e2.getY(1) + e2.getY(0)) / 2
        if (mFingersCenterX == 0f && mFingersCenterY == 0f) {
            mFingersCenterX = centerX
            mFingersCenterY = centerY
        }

        val changedCenterX = mFingersCenterX - centerX
        val changedCenterXValue = vsTouchScale.translationX - changedCenterX
        val fitTransX = 0f // to do 缩放中心修正~
        source.translationX = changedCenterXValue + fitTransX
        val changedCenterY = mFingersCenterY - centerY
        val changedCenterYValue = vsTouchScale.translationY - changedCenterY
        source.translationY = changedCenterYValue
    }


    /**
     * 处理结束双击的手指事件，进行图片放大到指定大小或者恢复到初始大小的收尾动画
     */
    private fun handleDoubleTapTouchResult() {
        val source = iSource ?: return
        val vsDefault = ViewState.read(source, ViewState.STATE_DEFAULT) ?: return
        val vsCurrent = ViewState.write(source, ViewState.STATE_CURRENT)

        if (vsCurrent.scaleY <= vsDefault.scaleY && vsCurrent.scaleX <= vsDefault.scaleX) {
            var expectedScale = (MAX_SCALE - vsDefault.scaleX) * 0.4f + vsDefault.scaleX

            // 横向超长图片双击无法看清楚的问题
            val imageOrientation = source.orientation ?: 0
            if (imageOrientation == LinearLayout.HORIZONTAL) {
                val viewState = ViewState.read(source, ViewState.STATE_DEFAULT)
                //图片在双击的时候放大的倍数，如果图片过长看不放大根本看不见 #45 hu670014125
                val scale = (viewState.width / viewState.height).toFloat()
                var maxScale = MAX_SCALE
                if (scale > 2.0f) {
                    maxScale = MAX_SCALE * scale / 2
                }
                expectedScale = (maxScale - vsDefault.scaleX) * 0.4f + vsDefault.scaleX
            }

            animSourceViewStateTransform(
                source,
                ViewState.write(source, ViewState.STATE_TEMP).scaleX(expectedScale).scaleY(
                    expectedScale
                )
            )
        } else {
            animSourceViewStateTransform(source, vsDefault)
        }
    }

    /**
     * 处理结束缩放旋转模式的手指事件，进行恢复到零旋转角度和大小收缩到正常范围以内的收尾动画<br></br>
     */
    private fun handleScaleTouchResult() {
        val source = iSource ?: return

        val vsDefault = ViewState.read(source, ViewState.STATE_DEFAULT) ?: return
        val vsCurrent = ViewState.write(source, ViewState.STATE_CURRENT)

        val endScaleX: Float
        val endScaleY: Float
        endScaleX = if (vsCurrent.scaleX < vsDefault.scaleX) vsDefault.scaleX else vsCurrent.scaleX
        endScaleY = if (vsCurrent.scaleY < vsDefault.scaleY) vsDefault.scaleY else vsCurrent.scaleY

        val vsTemp =
            ViewState.copy(vsDefault, ViewState.STATE_TEMP).scaleX(endScaleX).scaleY(endScaleY)
        if (source.width * vsCurrent.scaleX > mWidth) {
            val endTranslateX: Float
            val translateXEdge = vsCurrent.width * (vsCurrent.scaleX - 1) / 2
            if (vsCurrent.translationX > translateXEdge)
                endTranslateX = translateXEdge
            else if (vsCurrent.translationX < -translateXEdge)
                endTranslateX = -translateXEdge
            else
                endTranslateX = vsCurrent.translationX

            vsTemp.translationX(endTranslateX) // 缩放结果X轴比屏幕宽度长
        }
        if (source.height * vsCurrent.scaleY > mHeight) {
            val endTranslateY: Float
            val translateYBottomEdge =
                vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2
            val translateYTopEdge =
                mHeight.toFloat() - vsDefault.height * vsCurrent.scaleY / 2 - (vsDefault.height / 2).toFloat()

            if (vsCurrent.translationY > translateYBottomEdge)
                endTranslateY = translateYBottomEdge
            else if (vsCurrent.translationY < translateYTopEdge)
                endTranslateY = translateYTopEdge
            else
                endTranslateY = vsCurrent.translationY

        }
        //保持长图translationY
        if (source.proportion > 1f) {
            vsTemp.translationY(vsCurrent.translationY)
        }

        source.setTag(ViewState.STATE_TEMP, vsTemp)
        animSourceViewStateTransform(iSource, vsTemp)
        animBackgroundTransform(-0x1000000, 0)
    }

    /**
     * 处理结束拖拽模式的手指事件，进行超过边界则恢复到边界的收尾动画
     */
    private fun handleDragTouchResult() {
        if (iSource == null) return
        val vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT) ?: return
        val vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT)

        val endTranslateX: Float
        val endTranslateY: Float
        val imageOrientation = iSource?.orientation
        if (0 == imageOrientation) {
            val translateXEdge = vsDefault.width * (vsCurrent.scaleX - 1) / 2
            endTranslateX = when {
                vsCurrent.translationX > translateXEdge -> translateXEdge
                vsCurrent.translationX < -translateXEdge -> -translateXEdge
                else -> vsCurrent.translationX
            }

            endTranslateY = if (vsDefault.height * vsCurrent.scaleY <= mHeight) {
                vsDefault.translationY
            } else {
                val translateYBottomEdge =
                    vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2
                val translateYTopEdge =
                    mHeight.toFloat() - vsDefault.height * vsCurrent.scaleY / 2 - (vsDefault.height / 2).toFloat()

                when {
                    vsCurrent.translationY > translateYBottomEdge -> translateYBottomEdge
                    vsCurrent.translationY < translateYTopEdge -> translateYTopEdge
                    else -> vsCurrent.translationY
                }
            }
        } else if (1 == imageOrientation) {
            if (vsDefault.width * vsCurrent.scaleX <= mWidth) {
                endTranslateX = vsDefault.translationX
            } else {
                val translateXRightEdge =
                    vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2
                val translateXLeftEdge =
                    mWidth.toFloat() - vsDefault.width * vsCurrent.scaleX / 2 - (vsDefault.width / 2).toFloat()

                endTranslateX = when {
                    vsCurrent.translationX > translateXRightEdge -> translateXRightEdge
                    vsCurrent.translationX < translateXLeftEdge -> translateXLeftEdge
                    else -> vsCurrent.translationX
                }
            }

            val translateYBottomEdge =
                vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2
            val translateYTopEdge =
                mHeight.toFloat() - vsDefault.height * vsCurrent.scaleY / 2 - (vsDefault.height / 2).toFloat()

            if (vsCurrent.translationY > translateYBottomEdge)
                endTranslateY = translateYBottomEdge
            else if (vsCurrent.translationY < translateYTopEdge)
                endTranslateY = translateYTopEdge
            else
                endTranslateY = vsCurrent.translationY
        } else {
            return
        }
        if (vsCurrent.translationX == endTranslateX && vsCurrent.translationY == endTranslateY) {
            return // 如果没有变化跳过动画实行时间的触摸锁定
        }

        if (iSource!!.proportion > 1f) {
            iSource?.pivotY = (mHeight / 2).toFloat() - endTranslateY
        }

        animSourceViewStateTransform(
            iSource,
            ViewState.write(iSource, ViewState.STATE_TEMP).translationX(endTranslateX).translationY(
                endTranslateY
            )
        )

        animBackgroundTransform(-0x1000000, 0)
    }

    /**
     * 处理结束下拉退出的手指事件，进行退出图片查看或者恢复到初始状态的收尾动画<br></br>
     * 还需要还原背景色
     */
    private fun handleExitTouchResult() {
        if (iSource == null) return

        if (mExitRef > 0.85f) {//恢复
            val vsExit = ViewState.read(iSource, ViewState.STATE_EXIT)
            if (vsExit != null) animSourceViewStateTransform(iSource, vsExit, 50)
            animBackgroundTransform(-0x1000000, 0)
        } else {//退出
            val vsOrigin = ViewState.read(iSource, ViewState.STATE_ORIGIN)
            var dur = 300
            if (vsOrigin != null) {
                if (vsOrigin.alpha == 0f) {
                    vsOrigin.translationX(iSource!!.translationX)
                        .translationY(screenHeight.toFloat())
                    dur = 200
                }
                animSourceViewStateTransform(iSource, vsOrigin, dur)
            }

            animBackgroundTransform(0x00000000, STATE_EXIT_HIDING)
            (iSource!!.parent as FrameLayout).getChildAt(2).animate().alpha(0f)
                .setDuration(dur.toLong()).start()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mPagerPositionOffsetPixels = positionOffsetPixels

        onPageChangeListeners.forEach {
            it.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    /**
     * 每当ViewPager滑动到新的一页后，此方法会被触发<br></br>
     * 此刻必不可少的需要同步更新顶部索引，还原前一项后一项的状态等
     */
    override fun onPageSelected(position: Int) {
        iSource = adapter.mImageSparseArray.get(position)
        currentPosition = position

        indexProvider?.onPageChanged(this, position, mUrlList)

        val mLast = adapter.mImageSparseArray.get(position - 1)
        if (ViewState.read(mLast, ViewState.STATE_DEFAULT) != null) {
            ViewState.restoreByAnim(mLast, ViewState.STATE_DEFAULT, 0).create().start()
        }
        val mNext = adapter.mImageSparseArray.get(position + 1)
        if (ViewState.read(mNext, ViewState.STATE_DEFAULT) != null) {
            ViewState.restoreByAnim(mNext, ViewState.STATE_DEFAULT, 0).create().start()
        }

        if (onPageChangeListeners.isNotEmpty()) {
            for (onPageChangeListener in onPageChangeListeners) {
                onPageChangeListener.onPageSelected(position)
            }
        }
    }


    override fun onPageScrollStateChanged(state: Int) {
        if (onPageChangeListeners.isNotEmpty()) {
            for (onPageChangeListener in onPageChangeListeners) {
                onPageChangeListener.onPageScrollStateChanged(state)
            }
        }
    }

    internal inner class ImagePagerAdapter : PagerAdapter() {
        internal val mImageSparseArray = SparseArray<ImageView>()
        private var hasPlayBeginAnimation: Boolean = false

        override fun getCount(): Int {
            return mUrlList.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
            mImageSparseArray.remove(position)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = FrameLayout(container.context)
            container.addView(itemView)
            val imageView = ImageView(container.context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            itemView.addView(imageView)
            mImageSparseArray.put(position, imageView)

            var loadView: View? = loadingUIProvider?.initialView(container.context)
            if (loadView == null) {
                loadView = View(container.context) // 占位;errorView = getChildAt(2)
            }
            itemView.addView(loadView)

            val errorView = ImageView(container.context)
            errorView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            errorView.setImageResource(mErrorImageRes)
            itemView.addView(errorView)
            errorView.visibility = View.GONE

            if (setDefaultDisplayConfigs(imageView, position, hasPlayBeginAnimation)) {
                hasPlayBeginAnimation = true
            }
            return itemView
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        fun notifyItemChanged(position: Int) {
            val imageView = mImageSparseArray.get(position)
            if (imageView != null) {
                loader!!.load(
                    imageView.context, mUrlList[position],
                    LoadListener(imageView, false, false, position, this)
                )
            }

        }

        /**
         * 更新ViewPager中每项的当前状态，比如是否加载，比如是否加载失败
         *
         * @param position 当前项的位置
         * @param loading  是否显示加载中
         * @param error    是否显示加载失败
         */
        fun notifyItemChangedState(position: Int, loading: Boolean, error: Boolean) {
            val imageView = mImageSparseArray.get(position)
            if (imageView != null) {
                val itemView = imageView.parent as FrameLayout
                val loadView = itemView.getChildAt(1)

                if (loadingUIProvider != null) {
                    if (loading)
                        loadingUIProvider?.start(loadView)
                    else
                        loadingUIProvider?.stop(loadView)
                }

                val errorView = itemView.getChildAt(2) as ImageView
                errorView.alpha = 1f
                errorView.visibility = if (error) View.VISIBLE else View.GONE
            }
        }

        private fun setDefaultDisplayConfigs(
            imageView: ImageView,
            pos: Int,
            hasPlayBeginAnimation: Boolean
        ): Boolean {
            var isFindEnterImagePicture = false

            if (pos == initPosition && !hasPlayBeginAnimation) {
                isFindEnterImagePicture = true
                iSource = imageView
            }

            //退出时 移动目标
            var originRefView: ImageView? = null
            if (mImageGroupList != null) {
                originRefView = mImageGroupList!!.get(pos)
            }
            if (originRefView != null) {
                val location = IntArray(2)
                originRefView.getLocationOnScreen(location)
                imageView.translationX = location[0].toFloat()
                val locationYOfFullScreen = location[1]
                imageView.translationY = locationYOfFullScreen.toFloat()
                imageView.layoutParams.width = originRefView.width
                imageView.layoutParams.height = originRefView.height

                ViewState.write(imageView, ViewState.STATE_ORIGIN)
                    .width(originRefView.width)
                    .height(originRefView.height)

                val bmpMirror = originRefView.drawable
                if (bmpMirror != null) {
                    val bmpMirrorWidth = bmpMirror.bounds.width()
                    val bmpMirrorHeight = bmpMirror.bounds.height()
                    val vsThumb =
                        ViewState.write(imageView, ViewState.STATE_THUMB).width(bmpMirrorWidth)
                            .height(bmpMirrorHeight)
                            .translationX(((mWidth - bmpMirrorWidth) / 2).toFloat())
                            .translationY(((mHeight - bmpMirrorHeight) / 2).toFloat())

                    if (bmpMirror is Animatable) {
                        val constantState = bmpMirror.constantState
                        if (constantState != null) {
                            imageView.setImageDrawable(constantState.newDrawable())
                        } else {
                            imageView.setImageDrawable(null)
                        }
                    } else {
                        imageView.setImageDrawable(bmpMirror)
                    }

                    if (isFindEnterImagePicture) {
                        animSourceViewStateTransform(imageView, vsThumb)
                    } else {
                        ViewState.restore(imageView, vsThumb.mTag)
                    }
                }
            } else {
                if (isFindEnterImagePicture) {
                    //当未指定入口View，初始参数
                    val sh = screenHeight
                    val sw = screenWidth
                    val location = intArrayOf(sw / 2 - 250, sh / 2 - 250)
                    imageView.translationX = location[0].toFloat()
                    imageView.translationY = location[1].toFloat()
                    imageView.layoutParams.width = 500
                    imageView.layoutParams.height = 500
                }
                //退出时
                ViewState.write(imageView, ViewState.STATE_ORIGIN)
                    .alpha(0f)
                    .width(0).height(0)
                    .scaleXBy(0.4f)
                    .scaleY(0.4f)
            }

            val isPlayEnterAnimation = isFindEnterImagePicture
            // loadHighDefinitionPicture
            ViewState.clear(imageView, ViewState.STATE_DEFAULT)

            loader!!.load(
                imageView.context, mUrlList[pos],
                LoadListener(imageView, isPlayEnterAnimation, true, pos, this)
            )

            if (isPlayEnterAnimation) {
                animBackgroundTransform(-0x1000000, STATE_ENTER_DISPLAYING)
            }
            return isPlayEnterAnimation
        }
    }

    private class RefHandler internal constructor(ref: ImageWatcher) : Handler() {
        internal var mRef: WeakReference<ImageWatcher> = WeakReference(ref)

        override fun handleMessage(msg: Message) {
            val holder = mRef.get()
            if (holder != null) {
                when (msg.what) {
                    SINGLE_TAP_UP_CONFIRMED -> holder.onSingleTapConfirmed()
                    DATA_INITIAL -> holder.internalDisplayDataAfterLayout()
                }
            }
        }
    }

    fun setErrorImageRes(resErrorImage: Int) {
        mErrorImageRes = resErrorImage
    }

    override fun setBackgroundColor(color: Int) {
        mBackgroundColor = color
        super.setBackgroundColor(color)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        mWidth = w
        mHeight = h

        if (!isInitLayout) {
            isInitLayout = true
            mHandler.sendEmptyMessage(DATA_INITIAL)
        }
    }

    private fun internalDisplayDataAfterLayout() {
        if (initUrlList != null) {
            showInternal(initI, initImageGroupList!!, initUrlList!!)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (animImageTransform != null) animImageTransform!!.cancel()
        animImageTransform = null
        if (animBackground != null) animBackground!!.cancel()
        animBackground = null
        if (animFling != null) animFling!!.cancel()
        animFling = null
    }

    /**
     * 当界面处于图片查看状态需要在Activity中的[Activity.onBackPressed]
     * 将事件传递给ImageWatcher优先处理<br></br>
     * *ImageWatcher并没有从父View中移除
     * *当处于收尾动画执行状态时，消费返回键事件<br></br>
     * *当图片处于放大状态时，执行图片缩放到原始大小的动画，消费返回键事件<br></br>
     * *当图片处于原始状态时，退出图片查看，消费返回键事件<br></br>
     * *其他情况，ImageWatcher并没有展示图片
     */
    fun handleBackPressed(): Boolean {
        return !detachedParent && (isInTransformAnimation || iSource != null && visibility == View.VISIBLE && onSingleTapConfirmed())
    }

    /**
     * 将指定的ImageView形态(尺寸大小，缩放，旋转，平移，透明度)逐步转化到期望值
     */
    private fun animSourceViewStateTransform(
        view: ImageView?,
        vsResult: ViewState,
        dur: Int = 300
    ) {
        if (view == null) return
        animImageTransform?.cancel()

        animImageTransform = ViewState.restoreByAnim(view, vsResult.mTag, dur)
            .addListener(mAnimTransitionStateListener).create().apply {
                if (vsResult.mTag == ViewState.STATE_ORIGIN) {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            // 如果是退出查看操作，动画执行完后，原始被点击的ImageView恢复可见
                            visibility = View.GONE
                        }
                    })
                }
                start()
            }
    }

    /**
     * 执行ImageWatcher自身的背景色渐变至期望值[colorResult]的动画
     */
    private fun animBackgroundTransform(colorResult: Int, tag: Int) {
        if (animBackground != null) animBackground!!.cancel()
        val mCurrentBackgroundColor = mBackgroundColor
        animBackground = ValueAnimator.ofFloat(0f, 1f).setDuration(300)
        animBackground!!.addUpdateListener { animation ->
            val p = animation.animatedValue as Float
            setBackgroundColor(mColorEvaluator.evaluate(p, mCurrentBackgroundColor, colorResult))

            if (onStateChangedListeners.isNotEmpty()) {
                for (stateChangedListener in onStateChangedListeners) {
                    stateChangedListener.onStateChangeUpdate(
                        this@ImageWatcher,
                        iSource,
                        currentPosition,
                        displayingUri,
                        p,
                        tag
                    )
                }
            }
        }
        animBackground!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                if (onStateChangedListeners.isNotEmpty()) {
                    if (tag == STATE_ENTER_DISPLAYING) {
                        for (stateChangedListener in onStateChangedListeners) {
                            stateChangedListener.onStateChanged(
                                this@ImageWatcher,
                                currentPosition,
                                displayingUri,
                                tag
                            )
                        }
                    }
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (onStateChangedListeners.isNotEmpty()) {
                    if (tag == STATE_EXIT_HIDING) {
                        for (stateChangedListener in onStateChangedListeners) {
                            stateChangedListener.onStateChanged(
                                this@ImageWatcher,
                                currentPosition,
                                displayingUri,
                                tag
                            )
                        }
                    }
                }

                if (detachAffirmative && tag == STATE_EXIT_HIDING) {
                    detachedParent = true

                    if (parent != null) {
                        (parent as ViewGroup).removeView(this@ImageWatcher)
                    }
                }
            }
        })
        animBackground!!.start()
    }

    private fun animFling(view: ImageView, vsResult: ViewState, duration: Long) {
        var mduration = duration
        if (mduration > 400)
            mduration = 400
        else if (mduration < 100) mduration = 100
        if (animFling != null) animFling!!.cancel()
        animFling = ViewState.restoreByAnim(view, vsResult.mTag, 150)
            .addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    mTouchMode = TOUCH_MODE_AUTO_FLING
                }

                override fun onAnimationEnd(animation: Animator) {
                    mTouchMode = TOUCH_MODE_SCALE_LOCK
                    onUp(null)
                }
            }).create().apply {
                interpolator = decelerateInterpolator
                setDuration(mduration)
                start()
            }
    }

    internal inner class LoadListener(
        var imageView: ImageView,
        var isPlayEnterAnimation: Boolean,
        var isInit: Boolean,
        var pos: Int,
        var adapter: ImagePagerAdapter
    ) : LoadCallback {

        override fun onResourceReady(resource: Drawable) {
            val sourceDefaultWidth: Int
            val sourceDefaultHeight: Int
            val sourceDefaultTranslateX: Int
            val sourceDefaultTranslateY: Int
            val resourceImageWidth = resource.intrinsicWidth
            val resourceImageHeight = resource.intrinsicHeight

            //相对parentView宽高，计算比例
            val proportion =
                (resourceImageHeight.toFloat() / resourceImageWidth) / (mHeight.toFloat() / mWidth)

            Log.d("Debug :", "onResourceReady 比例： ----> $proportion")
            if (proportion > 1f) {//长图 设置pivotXY  为parent中心
                imageView.pivotX = (mWidth / 2).toFloat()
                imageView.pivotY = (mHeight / 2).toFloat()
            }

            imageView.proportion = proportion
            //水平图
            if (proportion < 1f) {
                sourceDefaultWidth = mWidth
                sourceDefaultHeight =
                    (sourceDefaultWidth * 1f / resourceImageWidth * resourceImageHeight).toInt()

                sourceDefaultTranslateX = (mWidth - sourceDefaultWidth) / 2
                sourceDefaultTranslateY = (mHeight - sourceDefaultHeight) / 2
                imageView.orientation = LinearLayout.HORIZONTAL
            } else {//长图
                sourceDefaultWidth = mWidth
                sourceDefaultHeight =
                    (sourceDefaultWidth * 1f / resourceImageWidth * resourceImageHeight).toInt()
                sourceDefaultTranslateX = 0
                sourceDefaultTranslateY = 0
                imageView.orientation = LinearLayout.VERTICAL
            }
            imageView.setImageDrawable(resource)
            adapter.notifyItemChangedState(pos, false, false)

            val vsDefault = ViewState.write(imageView, ViewState.STATE_DEFAULT)
                .width(sourceDefaultWidth)
                .height(sourceDefaultHeight)
                .translationX(sourceDefaultTranslateX.toFloat())
                .translationY(sourceDefaultTranslateY.toFloat())
            if (isPlayEnterAnimation && isInit) {
                animSourceViewStateTransform(imageView, vsDefault)
            } else {
                ViewState.restore(imageView, vsDefault.mTag)
                imageView.alpha = if (isInit) 0f else 1f
                imageView.animate().alpha((if (isInit) 1 else 0).toFloat()).start()
            }

            imageView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {}

                override fun onViewDetachedFromWindow(v: View) {
                    val displayingDrawable = imageView.drawable
                    if (displayingDrawable is Animatable) {
                        (displayingDrawable as Animatable).stop()
                    }
                }
            })

            //gif
            val displayingDrawable = imageView.drawable
            if (displayingDrawable is Animatable) {
                if (!(displayingDrawable as Animatable).isRunning) {
                    (displayingDrawable as Animatable).start()
                }
            }
        }


        override fun onLoadStarted(placeholder: Drawable?) {
            adapter.notifyItemChangedState(pos, true, false)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            adapter.notifyItemChangedState(pos, false, imageView.drawable == null)
        }
    }

    companion object {
        private val SINGLE_TAP_UP_CONFIRMED = 1
        private val DATA_INITIAL = 2

        internal const val MIN_SCALE = 0.4f // 显示中的图片最小缩小系数
        internal const val MAX_SCALE = 3.6f // 显示中的图片最大放大系数
        internal const val SCALE_SENSITIVITY = 0.5f// 缩放灵敏度 越小越灵敏
        internal const val EDGE_RESILIENCE = 0.16f // 边缘阻尼弹性 越小越难拉动

        //最低背景透明值
        const val MIN_BACKGROUND_ALPHA: Int = 0x77000000

        const val STATE_ENTER_DISPLAYING = 3
        const val STATE_EXIT_HIDING = 4

        private const val TOUCH_MODE_NONE = 0 // 无状态
        private const val TOUCH_MODE_DOWN = 1 // 按下
        private const val TOUCH_MODE_DRAG = 2 // 单点拖拽
        private const val TOUCH_MODE_SLIDE = 4 // 页面滑动
        private const val TOUCH_MODE_SCALE = 5 // 缩放
        private const val TOUCH_MODE_SCALE_LOCK = 6 // 缩放锁定
        private const val TOUCH_MODE_AUTO_FLING = 7 // 动画中
        private const val TOUCH_MODE_EXIT = 3 // 退出动作
    }
}

/**
 * @param v   当前被按的ImageView
 * @param uri 当前ImageView加载展示的图片地址
 * @param pos 当前ImageView在展示组中的位置
 */
// 当前展示图片长按的回调
typealias OnPictureLongPressListener = (v: ImageView, uri: Uri, pos: Int) -> Unit

internal var ImageView.orientation: Int?
    get() = getTag(R.id.image_orientation) as Int?
    set(value) {
        setTag(R.id.image_orientation, value)
    }
internal var ImageView.proportion: Float
    get() = getTag(R.id.hw_proportion) as Float? ?: 1f
    set(value) {
        setTag(R.id.hw_proportion, value)
    }

fun log(m: String) {
    if (BuildConfig.DEBUG) {
        Log.d("LLL", m)
    }
}