package com.rh.heji.network.interceptor

import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.*
import com.rh.heji.config.Config
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizedInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newBuilder = chain.request().newBuilder()
        val path = chain.request().url
        if (!path.pathSegments.contains("Login") && !path.pathSegments.contains("Register")) {
            newBuilder.header("Authorization", "Bearer ${Config.user.token}")
            Log.d("okhttp", "AuthorizedInterceptor:${Config.user.name},${Config.user.id}")
        }
        val request: Request = newBuilder
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