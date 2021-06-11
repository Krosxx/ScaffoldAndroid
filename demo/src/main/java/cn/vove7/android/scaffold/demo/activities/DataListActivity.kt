package cn.vove7.android.scaffold.demo.activities

import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.observe
import cn.vove7.android.scaffold.demo.BR
import cn.vove7.android.scaffold.demo.R
import cn.vove7.android.scaffold.demo.app.AppApi
import cn.vove7.android.scaffold.demo.databinding.ActivityDataListBinding
import cn.vove7.android.scaffold.ext.viewModelOf
import cn.vove7.android.scaffold.ui.base.BaseBindAdapter
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import cn.vove7.android.scaffold.ui.base.ScaffoldViewModel
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView

/**
 * # DataListActivity
 * Created on 2019/11/26
 *
 *
 * 处理流程：
 * 显示加载失败
 * 显示无数据
 * 刷新显示数据
 * 加载更多
 * 加载失败
 * 失败加载
 *
 * @author Vove
 */
class DataListActivity : ScaffoldActivity<ActivityDataListBinding>() {

    // lazy init
    private val vm: PagingViewModel by viewModelOf()

    private val listAdapter by lazy {
        PagingListAdapter().apply {
            setLoadMoreView(SimpleLoadMoreView())
            setOnLoadMoreListener({ loadMore() }, binding.listView)

            setOnItemClickListener { ada, view, position ->
                val i = ada.getItem(position)
                Toast.makeText(this@DataListActivity, "$i", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onObserveLiveData() {
        vm.uiData.observe(this) { ds ->
            (ds["data"] as Array<Int>?)?.also {
                if (it.isEmpty()) {
                    listAdapter.emptyView = TextView(this).apply {
                        text = "空空如也"
                        setOnClickListener {
                            refresh()
                        }
                    }
                    page++
                    binding.swipeView.isRefreshing = false
                    listAdapter.loadMoreComplete()
                    listAdapter.loadMoreEnd()
                } else {
                    if (binding.swipeView.isRefreshing) {
                        listAdapter.replaceData(it.toList())
                        binding.swipeView.isRefreshing = false
                    } else {
                        listAdapter.addData(it.toList())
                        page++
                    }
                    listAdapter.setEnableLoadMore(true)
                    listAdapter.loadMoreComplete()
                }

            }
            if ("failed" in ds) {
                if (page == -1) {
                    listAdapter.emptyView = TextView(this).apply {
                        text = "网络错误，点击重试"
                        setOnClickListener {
                            refresh(0)
                        }
                    }
                }
                if (binding.swipeView.isRefreshing) {
                    binding.swipeView.isRefreshing = false
                } else {
                    listAdapter.loadMoreFail()
                }
            }

        }
    }

    var page = -1
    override fun initView() {
        binding.listView.adapter = listAdapter
        binding.swipeView.setOnRefreshListener { refresh() }
        refresh(-1)
    }

    private fun loadMore() {
        vm.getDataList(page)
    }

    private fun refresh(p: Int = 1) {
        page = p
        listAdapter.setEnableLoadMore(false)
        binding.swipeView.isRefreshing = true
        vm.getDataList(page)
    }
}

class PagingViewModel : ScaffoldViewModel() {

    var f = 0

    //ViewMModel 处理结果 通知 UI 更新
    fun getDataList(page: Int) = apiCall({ AppApi.list(page) }) {
        if (page == 0) {
            emitUiState("data" to arrayOf<Int>())
            return@apiCall
        }
        if (page == -1) {
            emitUiState("failed" to true)
            return@apiCall
        }
        //非回调  同 if(isSuccesss)
        onSuccess {
            emitUiState("data" to getOrNull())
        }
        //非回调
        onFailure {
            when {
                page < 3 -> {//假数据
                    emitUiState("data" to arrayOf(-1, -1, -1, -1, 0))
                }
                f < 2 -> {
                    f++
                    emitUiState("failed" to true)
                }
                else -> {
                    emitUiState("data" to arrayOf<Int>())
                }
            }

        }
    }

}

/**
 *  自动绑定 -> <layout.data>
 */
class PagingListAdapter : BaseBindAdapter<Int>(R.layout.paging_item, BR.number)
