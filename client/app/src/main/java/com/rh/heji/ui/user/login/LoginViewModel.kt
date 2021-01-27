package com.rh.heji.ui.user.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.network.HejiNetwork
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun login(username: String, password: String) {
        launch({
            var requestBody = HejiNetwork.getInstance().login(username, password)
            var token = requestBody.data;
            AppCache.getInstance().saveToken(token)
            ToastUtils.showLong(requestBody.data)
        }, {
            ToastUtils.showLong("登陆错误:${it.message}")
        })

    }

    private fun launch(block: suspend () -> Unit, error: suspend (Throwable) -> Unit) = viewModelScope.launch {
        try {
            block()
        } catch (e: Throwable) {
            error(e)
        }
    }

}






