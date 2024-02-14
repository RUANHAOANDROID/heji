package com.rh.heji.network.interceptor

import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.*
import com.rh.heji.config.Config
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizedInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request().newBuilder()
            .build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            sendLoginBroadcast()
        }
        return response
    }

    private fun sendLoginBroadcast() {
        if (BuildConfig.DEBUG) {
            ToastUtils.showLong("请登录")
        }
        if (!Config.enableOfflineMode)
            App.viewModel.loginEvent.postValue(Event(401))
    }
}