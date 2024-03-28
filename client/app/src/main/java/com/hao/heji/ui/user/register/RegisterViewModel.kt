package com.hao.heji.ui.user.register

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.hao.heji.network.HttpManager
import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.utils.launch

internal class RegisterViewModel : BaseViewModel<RegisterAction, RegisterUiState>() {

    override fun doAction(action: RegisterAction) {

        when (action) {
            is RegisterAction.Register -> {
                register(action.username, action.tel, action.code, action.password)
            }
        }
    }

    private fun register(
        username: String,
        tel: String,
        code: String,
        password: String
    ) {
        var user = RegisterUser(
            name = username,
            tel = tel,
            password = encodePassword(password),
            code = code
        )
        launch({
            var response = HttpManager.getInstance().register(user)
            if (response.success()){
                user.password=password
                send(RegisterUiState.Success(user))
            }
        }, {
            ToastUtils.showLong(it.message)
        })

    }
    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }
}

