package cn.vove7.android.scaffold.ui.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.vove7.android.scaffold.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

@Keep
open class BaseBindAdapter<T>(layoutResId: Int, br: Int) :
    BaseQuickAdapter<T, BaseBindAdapter.BindViewHolder>(layoutResId) {

    /**
     * 将getItem()自动绑定到layout.data中
     * databining 布局中 data.name
     * 格式: BR.{name}
     */
    private val _br: Int = br

    override fun convert(helper: BindViewHolder, item: T) {
        helper.binding.run {
            setVariable(_br, item)
            executePendingBindings()
        }
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup?): View {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutResId, parent, false)
                ?: return super.getItemView(layoutResId, parent)
        return binding.root.apply {
            setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        }
    }

    @Keep
    class BindViewHolder(view: View) : BaseViewHolder(view) {
        val binding: ViewDataBinding
            get() = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as ViewDataBinding
    }
}