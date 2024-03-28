package com.hao.heji.ui.user.login

import com.hao.heji.ui.base.IAction
import com.hao.heji.ui.base.IUiState

/**
 *
 * @date 2022/9/30
 * @author 锅得铁
 * @since v1.0
 */
internal sealed interface LoginAction : IAction {
    class Login(val tel: String, val password: String) : LoginAction
    data object EnableOfflineMode : LoginAction
    data object GetServerUrl : LoginAction
    class SaveServerUrl(val address: String) : LoginAction
}

internal sealed interface LoginUiState : IUiState {
    class LoginSuccess(val token: String) : LoginUiState
    class LoginError(val t: Throwable) : LoginUiState
    data object OfflineRun : LoginUiState
    class ShowServerSetting(val url: String) : LoginUiState
}