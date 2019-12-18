package cn.daqinjia.android.scaffold.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.daqinjia.android.scaffold.net.ScaffoldApi
import kotlinx.coroutines.CoroutineScope

/**
 * # ScaffoldViewModel
 * Created on 2019/11/26
 *
 * @author Vove
 */
typealias UiData = Map<String, Any?>

open class ScaffoldViewModel : ViewModel(), ScaffoldApi {
    override val scope: CoroutineScope
        get() = viewModelScope

    /**
     * 负责传递数据到UI
     */
    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData> get() = _uiData

    /**
     * 通知UI
     */
    fun emitUiState(vararg data: Pair<String, Any?>) {
        _uiData.value = mapOf(*data)
    }

    fun emitUiState(status: String) {
        _uiData.value = mapOf(status to null)
    }

}
