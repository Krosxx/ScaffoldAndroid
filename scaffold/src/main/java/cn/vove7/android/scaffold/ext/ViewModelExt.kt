package cn.vove7.android.scaffold.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*

/**
 * # ViewModel
 * Created on 2019/11/25
 *
 * @author Vove
 */


inline fun <T> LiveData<T>.observer(
    owner: LifecycleOwner,
    crossinline doOnObserver: (T) -> Unit
) = observe(owner, Observer {
    doOnObserver(it)
})


inline fun <reified M : ViewModel> AppCompatActivity.viewModelOf(
    factoryViewModel: ViewModelProvider.Factory? = null

): Lazy<M> = lazy { ViewModelProviders.of(this, factoryViewModel).get(M::class.java) }


inline fun <reified M : ViewModel> Fragment.viewModelOf(
    factoryViewModel: ViewModelProvider.Factory? = null

): Lazy<M> = lazy { ViewModelProviders.of(this, factoryViewModel).get(M::class.java) }

