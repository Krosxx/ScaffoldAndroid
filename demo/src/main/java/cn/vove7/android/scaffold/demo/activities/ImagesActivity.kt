package cn.vove7.android.scaffold.demo.activities

import androidx.lifecycle.liveData
import androidx.recyclerview.widget.GridLayoutManager
import cn.vove7.android.scaffold.demo.R
import cn.vove7.android.scaffold.demo.databinding.ActivityImagesBinding
import cn.vove7.android.scaffold.demo.databinding.ItemImageBinding
import cn.vove7.android.scaffold.demo.repo.ImagesRepo
import cn.vove7.android.scaffold.ui.adapter.SBindAdapter
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity
import cn.vove7.android.scaffold.ui.base.ScaffoldViewModel
import com.bumptech.glide.Glide
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
        ImagesAdapter()
    }

    override fun onObserveLiveData() {
        vm.imgData.observe(this) {
            if (binding.recyclerView.adapter is ShimmerAdapter) {
                binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
                binding.recyclerView.adapter = adapter
            }
            adapter.setData(it)
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

class ImagesAdapter : SBindAdapter<ItemImageBinding, String>() {
    override fun onBind(binding: ItemImageBinding, item: String) {
        Glide.with(binding.img).load(item).into(binding.img)
        binding.img.setOnClickListener {
            PhotoViewActivity.start(binding.img.context, binding.img, item)
        }
    }
}
