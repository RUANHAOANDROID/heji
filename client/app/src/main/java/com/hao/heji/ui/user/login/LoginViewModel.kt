package com.hao.heji.ui.user.login

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.EncryptUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.config.LocalUser
import com.hao.heji.config.store.DataStoreManager
import com.hao.heji.data.repository.UserRepository
import com.hao.heji.network.HttpManager
import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.ui.user.JWTParse
import com.hao.heji.utils.launch
import com.hao.heji.utils.launchIO
import kotlinx.coroutines.launch


internal class LoginViewModel : BaseViewModel<LoginAction, LoginUiState>() {
    private val userRepository = UserRepository()
    override fun doAction(action: LoginAction) {
        when (action) {
            is LoginAction.Login -> {
                login(action.tel, action.password)
            }

            is LoginAction.EnableOfflineMode -> {
                enableOfflineMode()
            }

            is LoginAction.SaveServerUrl -> {
                viewModelScope.launch {
                    Config.setServerUrl(action.address)
                    HttpManager.getInstance().redirectServer()
                }

            }

            LoginAction.GetServerUrl -> {
                launchIO({
                    DataStoreManager.getServerUrl().collect {
                        send(LoginUiState.ShowServerSetting(it))
                    }
                })
            }
        }
    }

    private fun login(tel: String, password: String) {
        launch({
            var resp = userRepository.login(
                tel,
                encodePassword(password)
            )
            resp.data?.let {
                val newUser = JWTParse.getUser(it)
                Config.setUser(newUser)
                Config.enableOfflineMode(false)
                App.switchDataBase(newUser.id)
                send(LoginUiState.LoginSuccess(it))
            }
        }, {
            send(LoginUiState.LoginError(it))
        })

    }


    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }

    /**
     * 开启离线使用模式
     */
    private fun enableOfflineMode() {
        launchIO({
            Config.enableOfflineMode(true)
            Config.setUser(LocalUser)
            send(LoginUiState.OfflineRun)
        })

    }
}






