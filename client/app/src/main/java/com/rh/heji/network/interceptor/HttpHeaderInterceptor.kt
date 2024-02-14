package com.rh.heji.network.interceptor

import android.text.TextUtils
import android.util.Log
import com.rh.heji.App
import com.rh.heji.config.Config
import com.rh.heji.Event
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Header 拦截器
 */
class HttpHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var bearerToken: String = Config.token
        Log.d("OKHTTP", "HttpHeaderInterceptor $bearerToken")
        if (TextUtils.isEmpty(bearerToken)) {
            Log.d("OKHTTP", "HttpHeaderInterceptor not login")
        }
        val request: Request = chain.request().newBuilder()
            //.header("Content-Type", "application/json")
            .addHeader("Authorization", bearerToken)
            .build()
        if (!request.url.encodedPath.contains("login") && bearerToken.isEmpty()) {//非登录请求，且Token为空
            if (!request.url.encodedPath.contains("register")) {
                Log.d("OKHTTP", "HttpHeaderInterceptor register")
                sendLoginBroadcast()
            }
        }
        return chain.proceed(request)
    }

    private fun sendLoginBroadcast() {
        if (!Config.enableOfflineMode)
            App.viewModel.loginEvent.postValue(Event(401))
    }
}