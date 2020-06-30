package cn.daqinjia.android.scaffold.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 * # ScaffoldFragment
 * Created on 2019/11/26
 *
 * @author Vove
 */
abstract class ScaffoldFragment<VDB : ViewDataBinding>(vdbCls: KClass<VDB>)
    : Fragment(), ScaffoldPage<VDB> {

    override val layoutRes: Int = 0

    override val vdbCls: KClass<VDB> = vdbCls

    override lateinit var _binding: ViewDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return buildView(container, layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onPageCreate()
    }

    final override fun onPageCreate() {
        super.onPageCreate()
    }
}