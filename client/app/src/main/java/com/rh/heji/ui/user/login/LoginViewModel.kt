package com.rh.heji.ui.user.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.data.network.HejiNetwork
import com.rh.heji.utlis.http.basic.BaseCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class LoginViewModel : ViewModel() {
    fun login(username: String, password: String) {
        launch({
            var requestBody = HejiNetwork.getInstance().login(username, password)
            var token = requestBody.data;
            AppCache.getInstance().saveToken(token)
            ToastUtils.showLong(requestBody.data)
        }, {
            ToastUtils.showLong("ERROR 登陆错误")
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






