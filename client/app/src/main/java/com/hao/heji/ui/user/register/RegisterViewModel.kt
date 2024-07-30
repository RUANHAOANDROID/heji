package com.hao.heji.ui.user.register

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.data.repository.UserRepository
import com.hao.heji.utils.launch

internal class RegisterViewModel : BaseViewModel<RegisterUiState>() {
    private val userRepository = UserRepository()
    fun register(
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
            var response = userRepository.register(user)
            if (response.success()) {
                user.password = password
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

