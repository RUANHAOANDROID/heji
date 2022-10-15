package com.rh.heji.ui.setting.input.etc

import com.rh.heji.ui.base.IAction
import com.rh.heji.ui.base.IUiState

/**
 *
 * @date 2022/9/30
 * @author 锅得铁
 * @since v1.0
 */

internal sealed interface ETCUiState : IUiState {
    object InputSuccess : ETCUiState
    class InputError(val t: Throwable) : ETCUiState
}

internal sealed interface ETCAction : IAction {
    class RequestETCBill(val etcID: String, var month: String, val carID: String) : ETCAction
}