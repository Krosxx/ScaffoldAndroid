package cn.daqinjia.android.scaffold.demo.activities

import cn.daqinjia.android.scaffold.demo.BR
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.ui.base.BaseBindAdapter
import cn.daqinjia.android.scaffold.ui.base.NoBindingActivity
import com.github.ielse.imagewatcher.PhotoViewActivity
import com.github.ielse.imagewatcher.start
import kotlinx.android.synthetic.main.activity_images.*

/**
 * # ImagesActivity
 * Created on 2019/12/6
 *
 * @author Vove
 */
class ImagesActivity : NoBindingActivity() {

    private val adapter by lazy {
        ImagesAdapter().apply {
            setNewData(
                listOf(
                    "https://images.unsplash.com/photo-1558981001-1995369a39cd?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
                    ,
                    "https://images.unsplash.com/flagged/photo-1575556809963-3d9e5730eda0?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
                    ,
                    "https://images.unsplash.com/photo-1575483893529-3a9a8f8f2da5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
                    ,
                    "https://images.unsplash.com/photo-1575490937262-344efe96d264?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
                    ,
                    "https://images.unsplash.com/photo-1575552141920-05e428c50bf3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
                    ,
                    "https://images.unsplash.com/photo-1575525351638-776e90838483?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
                    ,
                    "http://wx4.sinaimg.cn/orj360/007kVUJhly1g1inspvu5zj30v93rbnpd.jpg"
                )
            )
            setOnItemClickListener { _, view, position ->
                PhotoViewActivity.start(
                    this@ImagesActivity,
                    view.findViewById(R.id.img),
                    data, position
                )
            }
        }
    }

    override fun initView() {
        recycler_view.adapter = adapter
    }

    override val layoutRes: Int
        get() = R.layout.activity_images

}

class ImagesAdapter : BaseBindAdapter<String>(R.layout.item_image, BR.imgUrl)
