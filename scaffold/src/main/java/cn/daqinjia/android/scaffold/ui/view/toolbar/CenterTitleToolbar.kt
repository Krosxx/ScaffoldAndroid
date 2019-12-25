package cn.daqinjia.android.scaffold.ui.view.toolbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.Menu
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import cn.daqinjia.android.scaffold.R
import cn.daqinjia.android.common.ext.show
import kotlinx.android.synthetic.main.layout_toolbar_center_title.view.*

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

    private val toolbarView = LayoutInflater.from(context)
        .inflate(R.layout.layout_toolbar_center_title, this, true)

    init {
        toolbarView.apply {
            //防止存在 padding
            setContentInsetsAbsolute(0, 0)
            menuView.apply {
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
            }
        }
    }

    /**
     * 在Toolbar(父类构造函数可能会调用)
     * 存储属性 解决Toolbar 构造函数 setTitle 时，toolbarView 未初始化
     */
    override fun setTitle(title: CharSequence?) {
        setTag(R.id.titleView, title)
        toolbarView?.titleView?.text = title
    }

    override fun inflateMenu(resId: Int) {
        setTag(R.id.menuView, resId)
        // 在Toolbar构造函数调用此方法时 toolbarView 为空
        if (toolbarView != null) {
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
        return toolbarView.menuView.menu
    }

    override fun setNavigationOnClickListener(listener: OnClickListener?) {
        toolbarView.nav_icon.setOnClickListener(listener)
    }

    override fun setNavigationIcon(icon: Drawable?) {
        setTag(R.id.nav_icon, icon)
        toolbarView?.nav_icon?.show()
        toolbarView?.nav_icon?.setImageDrawable(icon)
    }


    /**
     * 菜单事件
     */
    private var mOnMenuItemClickListener: OnMenuItemClickListener? = null

    override fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        mOnMenuItemClickListener = listener
    }

}