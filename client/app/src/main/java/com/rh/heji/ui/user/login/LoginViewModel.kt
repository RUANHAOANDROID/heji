package com.rh.heji.ui.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : BaseViewModel() {
    private val loginLiveData: MediatorLiveData<String> = MediatorLiveData()
    fun login(username: String, password: String): LiveData<String> {
        launch({
            var requestBody = HejiNetwork.getInstance().login(username, password)
            var token = requestBody.data
            loginLiveData.value = token
            withContext(Dispatchers.IO) {
                AppCache.instance.token.save(token)
            }
            ToastUtils.showLong(requestBody.data)
            LogUtils.w(token)
        }, {
            ToastUtils.showLong("登陆错误:${it.message}")
        })
        launch({}, {})
        return loginLiveData
    }

}






