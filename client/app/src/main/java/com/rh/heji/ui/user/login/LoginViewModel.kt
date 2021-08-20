package com.rh.heji.ui.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.AppViewModule
import com.rh.heji.currentUser
import com.rh.heji.network.HejiNetwork
import com.rh.heji.security.Token
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : BaseViewModel() {
    private val loginLiveData: MediatorLiveData<String> = MediatorLiveData()
    fun login(username: String, password: String): LiveData<String> {
        launch({
            var requestBody = HejiNetwork.getInstance().login(username, password)
            var token = requestBody.date
            loginLiveData.value = token
            withContext(Dispatchers.IO) {
               Token.encodeToken(token)
                currentUser = JWTParse.getUser(token)
            }
            ToastUtils.showLong(requestBody.date)
            LogUtils.w(token)
        }, {
            ToastUtils.showLong("登陆错误:${it.message}")
        })
        launch({}, {})
        return loginLiveData
    }

}






