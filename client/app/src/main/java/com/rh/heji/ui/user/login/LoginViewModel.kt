package com.rh.heji.ui.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.currentUser
import com.rh.heji.network.HejiNetwork
import com.rh.heji.security.Token
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO

class LoginViewModel : BaseViewModel() {
    private val loginLiveData: MediatorLiveData<String> = MediatorLiveData()
    fun login(username: String, password: String): LiveData<String> {
        launchIO({
            var requestBody = HejiNetwork.getInstance().login(username, password)
            var token = requestBody.data
            Token.encodeToken(token)
            currentUser = JWTParse.getUser(token)
            ToastUtils.showLong(requestBody.data)
            loginLiveData.postValue(token)
        }, {
            ToastUtils.showLong("登陆错误:${it.message}")
        })
        launch({}, {})
        return loginLiveData
    }

}






