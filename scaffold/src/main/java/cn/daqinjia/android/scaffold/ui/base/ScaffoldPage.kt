package cn.daqinjia.android.scaffold.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


interface ScaffoldPage<VDB : ViewDataBinding> {
    var _binding: ViewDataBinding

    val binding: VDB get() = _binding as VDB

    val layoutRes: Int

    fun buildView(container: ViewGroup? = null, layoutInflater: LayoutInflater): View {
        val inflateView by lazy {
            layoutInflater.inflate(layoutRes, container, false)
        }
        return try {
            DataBindingUtil.inflate<VDB>(layoutInflater, layoutRes, container, false)?.let {
                _binding = it
                it.root
            } ?: inflateView
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