package cn.vove7.android.scaffold.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cn.vove7.android.scaffold.ui.base.ScaffoldPage

/**
 * # BaseBindAdapter
 *
 * @author Vove
 * @date 2021/9/11
 */
abstract class SBindAdapter<TVB : ViewBinding, TM> : RecyclerView.Adapter<SBindAdapter.VH<TVB>>() {

    private val dataList: MutableList<TM> = mutableListOf()

    private val vbCls by lazy {
        ScaffoldPage.getPageViewBindingCls<TVB>(this::class.java)!!
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): VH<TVB> = VH(newTVB(parent))

    private fun newTVB(parent: ViewGroup): TVB {
        return vbCls.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, LayoutInflater.from(parent.context), parent, false) as TVB
    }

    override fun onBindViewHolder(holder: VH<TVB>, position: Int) {
        onBind(holder.binding, dataList[position])
    }

    abstract fun onBind(binding: TVB, item: TM)

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<TM>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    class VH<T : ViewBinding>(
        val binding: T
    ) : RecyclerView.ViewHolder(binding.root)
}