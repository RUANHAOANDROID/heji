package com.hao.heji.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    /**
     * Ui state launcher
     */
    private var _uiState = MutableSharedFlow<O>()

    /**
     *
     * Provide to view
     * @return LiveData<O>
     */
    fun uiState(): SharedFlow<O> = _uiState

    abstract fun doAction(action: I)

    fun send(o: O) {
        viewModelScope.launch {
            _uiState.emit(o)
        }
    }

}