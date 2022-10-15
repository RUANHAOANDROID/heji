package com.rh.heji.ui.user.register

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch

class RegisterViewModel : BaseViewModel<RegisterAction, RegisterUiState>() {

    override fun doAction(action: RegisterAction) {
        super.doAction(action)
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
            var response = HejiNetwork.getInstance().register(user)
            val body = response.data.apply {
                this.password = password//本地输入的未加密的密码
            }
            send(RegisterUiState.Success(body))
        }, {
            ToastUtils.showLong(it.message)
        })

    }

    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }
}

