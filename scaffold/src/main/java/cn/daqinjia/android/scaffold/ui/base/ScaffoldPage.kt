package cn.daqinjia.android.scaffold.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlin.reflect.KClass


interface ScaffoldPage<VDB : ViewDataBinding> {
    val vdbCls: KClass<VDB>
    var _binding: ViewDataBinding

    val binding: VDB get() = _binding as VDB

    val layoutRes: Int

    fun buildView(container: ViewGroup? = null, layoutInflater: LayoutInflater): View? {
        val inflateView by lazy {
            //支持 不指定布局资源
            if (layoutRes == 0) null
            else layoutInflater.inflate(layoutRes, container, false)
        }
        return try {
            val m = vdbCls.java.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            _binding = m.invoke(null, layoutInflater, container, false) as VDB
            _binding.root
        } catch (e: Throwable) {
            inflateView
        }

    }

    fun onObserveLiveData() {}
    fun bindBinding() {}
    fun initView() {}

    fun onPageCreate() {
        bindBinding()
        initView()
        onObserveLiveData()
    }


}