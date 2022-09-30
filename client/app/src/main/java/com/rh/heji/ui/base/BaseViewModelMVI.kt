package com.rh.heji.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.LogUtils

/**
 * Base ViewModel
 * @date 2022/9/29
 * @author 锅得铁
 * @since v1.0
 * @param I input ui action
 * @param O output ui state
 */
abstract class BaseViewModelMVI<I : IAction, O : IUiState> : ViewModel() {

    protected var _uiState = MutableLiveData<O>()

    fun uiState(): LiveData<O> = _uiState

    open fun doAction(action: I) {
        LogUtils.d(action.id())
    }

    override fun onCleared() {
        super.onCleared()
    }

}