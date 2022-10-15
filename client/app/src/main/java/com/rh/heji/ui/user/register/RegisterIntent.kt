package com.rh.heji.ui.user.register

import com.rh.heji.ui.base.IAction
import com.rh.heji.ui.base.IUiState
import com.rh.heji.ui.user.JWTParse

/**
 * TODO
 * @date 2022/9/29
 * @author 锅得铁
 * @since v1.0
 */
internal sealed interface RegisterUiState : IUiState {
    class Success(val user: RegisterUser) : RegisterUiState
    class Error(val throwable: Throwable) : RegisterUiState
}

internal sealed interface RegisterAction : IAction {
    class Register(
        val username: String,
        val tel: String,
        val code: String,
        var password: String
    ) : RegisterAction
}