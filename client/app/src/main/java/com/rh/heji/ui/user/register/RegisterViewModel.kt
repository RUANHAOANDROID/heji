package com.rh.heji.ui.user.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch
import java.io.Serializable

class RegisterViewModel : BaseViewModel() {
    private val registerLiveData = MutableLiveData<RegisterUser>()
    fun registerResult(): LiveData<RegisterUser> {
        return registerLiveData
    }

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
            var response = HejiNetwork.getInstance().register(user)
            val body = response.data.apply {
                this.password = password//本地输入的未加密的密码
            }
            registerLiveData.postValue(body)
        }, {
            ToastUtils.showLong(it.message)
        })

    }

    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }
}

data class RegisterUser(
    var name: String,
    var password: String,
    var tel: String,
    var code: String
) : Serializable