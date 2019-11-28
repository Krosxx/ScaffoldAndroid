package cn.daqinjia.android.scaffold.demo.activities

import android.widget.Toast
import androidx.lifecycle.observe
import cn.daqinjia.android.scaffold.demo.BR
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.databinding.ActivityDataListBinding
import cn.daqinjia.android.scaffold.ext.viewModelOf
import cn.daqinjia.android.scaffold.demo.app.Api
import cn.daqinjia.android.scaffold.ui.base.BaseBindAdapter
import cn.daqinjia.android.scaffold.ui.base.ScaffoldActivity
import cn.daqinjia.android.scaffold.ui.base.ScaffoldViewModel
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView
import kotlinx.android.synthetic.main.activity_data_list.*

/**
 * # DataListActivity
 * Created on 2019/11/26
 *
 * @author Vove
 */
class DataListActivity : ScaffoldActivity<ActivityDataListBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_data_list

    // lazy init
    private val vm: PagingViewMModel by viewModelOf()

    private val listAdapter by lazy {
        PagingListAdapter().apply {
            setLoadMoreView(SimpleLoadMoreView())
            setOnLoadMoreListener({ loadMore() }, listView)
            setOnItemChildClickListener { ada, view, position ->

                val i = ada.getItem(position)
                when (view.id) {
                    R.id.button -> {
                        Toast.makeText(this@DataListActivity, "button $i", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(this@DataListActivity, "$i", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    override fun onObserveLiveData() {
        vm.uiData.observe(this) { ds ->
            (ds["data"] as Array<Int>?)?.also {
                if (it.isEmpty()) {
                    listAdapter.loadMoreComplete()
                    listAdapter.loadMoreEnd()
                } else {
                    if (swipeView.isRefreshing) {
                        listAdapter.replaceData(it.toList())
                        swipeView.isRefreshing = false
                    } else {
                        listAdapter.addData(it.toList())
                    }
                    listAdapter.setEnableLoadMore(true)
                    listAdapter.loadMoreComplete()
                }

            }
            if ("failed" in ds) {
                if (swipeView.isRefreshing) {
                    swipeView.isRefreshing = false
                } else {
                    listAdapter.loadMoreFail()
                }
            }

        }

    }

    override fun initView() {
        listView.adapter = listAdapter
        swipeView.setOnRefreshListener { refresh() }
        refresh()
    }


    private fun loadMore() {
        vm.loadMore()
    }

    private fun refresh() {
        swipeView.isRefreshing = true
        vm.refresh()
    }
}

class PagingViewMModel : ScaffoldViewModel() {
    var page = 0

    fun loadMore() = apiCall({ Api.list(page) }) {
        if (it.isSuccess && it.data != null) {
            page++
            emitUiState("data" to it.data)
        } else {
            emitUiState("failed" to true)
        }
    }

    fun refresh() {
        page = 0
        loadMore()
    }

}

/**
 *  自动绑定 -> <layout.data>
 */
class PagingListAdapter : BaseBindAdapter<Int>(R.layout.paging_item, BR.number) {

}
