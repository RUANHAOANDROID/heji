package com.rh.heji.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.utlis.runMainThread
import kotlinx.coroutines.launch

/**
 * Base ViewModel
 * @date 2022/9/29
 * @author 锅得铁
 * @since v1.0
 * @param I input ui action
 * @param O output ui state
 */
abstract class BaseViewModel<I : IAction, O : IUiState> : ViewModel() {

    protected var _uiState = MutableLiveData<O>()

    val uiState: LiveData<O> = _uiState

    open fun doAction(action: I) {
        LogUtils.d("${action.id()}")
    }


    /**
     * 通过setValue 发送liveData中所有的值，大部分场景中使用send
     *
     * @param o
     */

    fun send(o: O) {
        LogUtils.d(o.id())
        viewModelScope.launch {
            runMainThread {
                _uiState.value = o
            }
        }
    }

    /**
     *
     * 仅发射最后一个值
     * @param o
     */
    fun sendLast(o: O) {
        LogUtils.d(o.id())
        _uiState.postValue(o)
    }

    override fun onCleared() {
        super.onCleared()
    }

}