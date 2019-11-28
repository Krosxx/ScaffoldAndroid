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
        return DataBindingUtil.inflate<VDB>(layoutInflater, layoutRes, container, false)?.let {
            _binding = it
            it.root
        } ?: layoutInflater.inflate(layoutRes, container, false)
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