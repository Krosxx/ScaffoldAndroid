package cn.vove7.android.scaffold.demo.activities

import androidx.lifecycle.liveData
import androidx.recyclerview.widget.GridLayoutManager
import cn.vove7.android.scaffold.demo.BR
import cn.vove7.android.scaffold.demo.R
import cn.vove7.android.scaffold.demo.databinding.ActivityImagesBinding
import cn.vove7.android.scaffold.demo.repo.ImagesRepo
import cn.vove7.android.scaffold.ui.base.BaseBindAdapter
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import cn.vove7.android.scaffold.ui.base.ScaffoldViewModel
import com.cooltechworks.views.shimmer.ShimmerAdapter
import com.github.ielse.imagewatcher.PhotoViewActivity
import com.github.ielse.imagewatcher.start
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * # ImagesActivity
 * Created on 2019/12/6
 *
 * @author Vove
 */
class ImagesActivity : ScaffoldActivity<ActivityImagesBinding>() {

    //koin 注入
    private val vm: ImagesViewModel by viewModel()

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
            if (binding.recyclerView.adapter is ShimmerAdapter) {
                binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
                binding.recyclerView.adapter = adapter
            }
            adapter.setNewData(it)
        }
    }

    override fun initView() {
        binding.recyclerView.showShimmerAdapter()
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
