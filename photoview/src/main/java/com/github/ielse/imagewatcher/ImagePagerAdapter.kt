//package com.github.ielse.imagewatcher
//
//import android.graphics.drawable.Animatable
//import android.net.Uri
//import android.support.v4.view.PagerAdapter
//import android.util.SparseArray
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.ImageView
//import com.github.ielse.imagewatcher.ui.LoadingUIProvider
//
//internal class ImagePagerAdapter(
//        val mUrlList: MutableList<Uri>,
//        val loader: ImageWatcher.Loader,
//        val loadingUIProvider: LoadingUIProvider,
//        val
//) : PagerAdapter() {
//    internal val mImageSparseArray = SparseArray<ImageView>()
//    private var hasPlayBeginAnimation: Boolean = false
//
//    override fun getCount(): Int {
//        return mUrlList.size
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as View)
//        mImageSparseArray.remove(position)
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val itemView = FrameLayout(container.context)
//        container.addView(itemView)
//        val imageView = ImageView(container.context)
//        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//
////            imageView.pivotX = (screenWidth / 2).toFloat()
////            imageView.pivotY = (screenHeight / 2).toFloat()
//        itemView.addView(imageView)
//        mImageSparseArray.put(position, imageView)
//
//        var loadView: View? = loadingUIProvider.initialView(container.context)
//        if (loadView == null) {
//            loadView = View(container.context) // 占位;errorView = getChildAt(2)
//        }
//        itemView.addView(loadView)
//
//        val errorView = ImageView(container.context)
//        errorView.scaleType = ImageView.ScaleType.CENTER_INSIDE
//        errorView.setImageResource(mErrorImageRes)
//        itemView.addView(errorView)
//        errorView.visibility = View.GONE
//
//        if (setDefaultDisplayConfigs(imageView, position, hasPlayBeginAnimation)) {
//            hasPlayBeginAnimation = true
//        }
//        return itemView
//    }
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view === `object`
//    }
//
//    fun notifyItemChanged(position: Int) {
//        val imageView = mImageSparseArray.get(position)
//        if (imageView != null) {
//            loader.load(imageView.context, mUrlList[position],
//                    ImageWatcher.LoadListener(imageView, false, false, position, this))
//        }
//
//    }
//
//    /**
//     * 更新ViewPager中每项的当前状态，比如是否加载，比如是否加载失败
//     *
//     * @param position 当前项的位置
//     * @param loading  是否显示加载中
//     * @param error    是否显示加载失败
//     */
//    fun notifyItemChangedState(position: Int, loading: Boolean, error: Boolean) {
//        val imageView = mImageSparseArray.get(position)
//        if (imageView != null) {
//            val itemView = imageView.parent as FrameLayout
//            val loadView = itemView.getChildAt(1)
//
//            if (loading) loadingUIProvider.start(loadView)
//            else loadingUIProvider.stop(loadView)
//
//            val errorView = itemView.getChildAt(2) as ImageView
//            errorView.alpha = 1f
//            errorView.visibility = if (error) View.VISIBLE else View.GONE
//        }
//    }
//
//    private fun setDefaultDisplayConfigs(imageView: ImageView, pos: Int, hasPlayBeginAnimation: Boolean): Boolean {
//        var isFindEnterImagePicture = false
//
//        if (pos == initPosition && !hasPlayBeginAnimation) {
//            isFindEnterImagePicture = true
//            iSource = imageView
//        }
//
//        //退出时 移动目标
//        var originRefView: ImageView? = null
//        if (mImageGroupList != null) {
//            originRefView = mImageGroupList!!.get(pos)
//        }
//        if (originRefView != null) {
//            val location = IntArray(2)
//            originRefView.getLocationOnScreen(location)
//            imageView.translationX = location[0].toFloat()
//            val locationYOfFullScreen = location[1]
//            imageView.translationY = locationYOfFullScreen.toFloat()
//            imageView.layoutParams.width = originRefView.width
//            imageView.layoutParams.height = originRefView.height
//
//            ViewState.write(imageView, ViewState.STATE_ORIGIN)
//                    .width(originRefView.width)
//                    .height(originRefView.height)
//
//            val bmpMirror = originRefView.drawable
//            if (bmpMirror != null) {
//                val bmpMirrorWidth = bmpMirror.bounds.width()
//                val bmpMirrorHeight = bmpMirror.bounds.height()
//                val vsThumb = ViewState.write(imageView, ViewState.STATE_THUMB).width(bmpMirrorWidth).height(bmpMirrorHeight)
//                        .translationX(((mWidth - bmpMirrorWidth) / 2).toFloat()).translationY(((mHeight - bmpMirrorHeight) / 2).toFloat())
//
//                if (bmpMirror is Animatable) {
//                    val constantState = bmpMirror.constantState
//                    if (constantState != null) {
//                        imageView.setImageDrawable(constantState.newDrawable())
//                    } else {
//                        imageView.setImageDrawable(null)
//                    }
//                } else {
//                    imageView.setImageDrawable(bmpMirror)
//                }
//
//                if (isFindEnterImagePicture) {
//                    animSourceViewStateTransform(imageView, vsThumb)
//                } else {
//                    ViewState.restore(imageView, vsThumb.mTag)
//                }
//            }
//        } else {
//            if (isFindEnterImagePicture) {
//                //当未指定入口View，初始参数
//                val sh = screenHeight
//                val sw = screenWidth
//                val location = intArrayOf(sw / 2 - 250, sh / 2 - 250)
//                imageView.translationX = location[0].toFloat()
//                imageView.translationY = location[1].toFloat()
//                imageView.layoutParams.width = 500
//                imageView.layoutParams.height = 500
//            }
//            //退出时
//            ViewState.write(imageView, ViewState.STATE_ORIGIN)
//                    .alpha(0f)
//                    .width(0).height(0)
//                    .scaleXBy(0.4f)
//                    .scaleY(0.4f)
//        }
//
//        val isPlayEnterAnimation = isFindEnterImagePicture
//        // loadHighDefinitionPicture
//        ViewState.clear(imageView, ViewState.STATE_DEFAULT)
//
//        loader.load(imageView.context, mUrlList[pos],
//                ImageWatcher.LoadListener(imageView, isPlayEnterAnimation, true, pos, this))
//
//        if (isPlayEnterAnimation) {
//            animBackgroundTransform(-0x1000000, STATE_ENTER_DISPLAYING)
//        }
//        return isPlayEnterAnimation
//    }
//}
