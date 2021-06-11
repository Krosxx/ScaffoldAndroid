package cn.vove7.android.scaffold.ui.view.toolbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.Menu
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import cn.vove7.android.common.ext.show
import cn.vove7.android.scaffold.R
import cn.vove7.android.scaffold.databinding.LayoutToolbarCenterTitleBinding

/**
 * # CenterTitleToolbar
 *
 * 继承Toolbar 实现标题居中
 * 注意：
 * 1. 标题长度需要自行控制
 * 2. 右侧菜单只允许显示一个图标
 * 3. 不可以在xml指定menu
 *
 * Created on 2019/12/19
 * @author Vove
 */
class CenterTitleToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.toolbarStyle
) : Toolbar(context, attrs, defStyleAttr) {

    //放在 init 之上
    private val mMenuViewItemClickListener =
        ActionMenuView.OnMenuItemClickListener { item ->
            mOnMenuItemClickListener?.onMenuItemClick(item) ?: false
        }

    private var vb: LayoutToolbarCenterTitleBinding? = null

    init {
        vb = LayoutToolbarCenterTitleBinding.inflate(
            LayoutInflater.from(context),
            this, true
        )
        vb?.root?.apply {
            //防止存在 padding
            setContentInsetsAbsolute(0, 0)
            vb?.menuView?.apply {
                this.popupTheme = this@CenterTitleToolbar.popupTheme
                this.setOnMenuItemClickListener(mMenuViewItemClickListener)
            }
            post {
                //
                getTag(R.id.titleView)?.also {
                    title = it as CharSequence?
                }
                getTag(R.id.nav_icon)?.also {
                    navigationIcon = it as Drawable?
                }
                getTag(R.id.menuView)?.also {
                    inflateMenu(it as Int)
                }
                invalidate()
            }
        }
    }

    /**
     * 在Toolbar(父类构造函数可能会调用)
     * 存储属性 解决Toolbar 构造函数 setTitle 时，toolbarView 未初始化
     */
    override fun setTitle(title: CharSequence?) {
        setTag(R.id.titleView, title)
        kotlin.runCatching {
            vb?.titleView?.text = title
        }
    }

    override fun inflateMenu(resId: Int) {
        setTag(R.id.menuView, resId)
        // 在Toolbar构造函数调用此方法时 toolbarView 为空
        @Suppress("SENSELESS_COMPARISON")
        if (vb != null) {
            super.inflateMenu(resId)
        }
    }

    override fun getTitle(): CharSequence? {
        return getTag(R.id.titleView) as CharSequence?
    }

    override fun setSubtitle(subtitle: CharSequence?) {
        //unsupport
    }

    override fun getMenu(): Menu {
        return vb?.menuView?.menu!!
    }

    override fun setNavigationOnClickListener(listener: OnClickListener?) {
        vb?.navIcon?.setOnClickListener(listener)
    }

    override fun setNavigationIcon(icon: Drawable?) {
        setTag(R.id.nav_icon, icon)
        vb?.navIcon?.show()
        vb?.navIcon?.setImageDrawable(icon)
    }


    /**
     * 菜单事件
     */
    private var mOnMenuItemClickListener: OnMenuItemClickListener? = null

    override fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        mOnMenuItemClickListener = listener
    }

}