package cn.daqinjia.android.scaffold.demo.activities

import androidx.lifecycle.liveData
import androidx.lifecycle.observe
import cn.daqinjia.android.scaffold.demo.BR
import cn.daqinjia.android.scaffold.demo.R
import cn.daqinjia.android.scaffold.demo.repo.ImagesRepo
import cn.daqinjia.android.scaffold.ui.base.BaseBindAdapter
import cn.daqinjia.android.scaffold.ui.base.NoBindingActivity
import cn.daqinjia.android.scaffold.ui.base.ScaffoldViewModel
import com.github.ielse.imagewatcher.PhotoViewActivity
import com.github.ielse.imagewatcher.start
import kotlinx.android.synthetic.main.activity_images.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * # ImagesActivity
 * Created on 2019/12/6
 *
 * @author Vove
 */
class ImagesActivity : NoBindingActivity() {

    //skio 注入
    private val vm :ImagesViewModel by viewModel()

    private val adapter by lazy {
        ImagesAdapter().apply {
            setOnItemClickListener { _, view, position ->
                PhotoViewActivity.start(
                    this@ImagesActivity,
                    view.findViewById(R.id.img),
                    data, position
                )
            }
        }
    }

    override fun onObserveLiveData() {
        vm.imgData.observe(this) {
            adapter.setNewData(it)
        }
    }

    override fun initView() {
        recycler_view.adapter = adapter
    }

    override val layoutRes: Int
        get() = R.layout.activity_images

}

class ImagesViewModel(imgRepo: ImagesRepo) : ScaffoldViewModel() {
    val imgData = liveData {
        emit(imgRepo.getImages())
    }
}

class ImagesAdapter : BaseBindAdapter<String>(R.layout.item_image, BR.imgUrl)
