package com.rh.heji.utlis.http.basic

import android.text.TextUtils
import android.util.Log
import com.rh.heji.App
import com.rh.heji.AppViewModule
import com.rh.heji.Event
import com.rh.heji.security.Token
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Header 拦截器
 */
class HttpHeaderInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var bearerToken: String = Token.decodeToken()
        Log.d("OKHTTP", "HttpHeaderInterceptor $bearerToken")
        if (TextUtils.isEmpty(bearerToken)) {
            Log.d("OKHTTP", "HttpHeaderInterceptor not login")
        }
        val request: Request = chain.request().newBuilder()
            //.header("Content-Type", "application/json")
            .addHeader("Authorization", bearerToken)
            .build()
        if (!request.url.encodedPath.contains("login") && bearerToken.isNullOrEmpty()){//非登录请求，且Token为空
            if (!request.url.encodedPath.contains("register")){
                Log.d("OKHTTP","HttpHeaderInterceptor register")
                sendLoginBroadcast()
            }
        }
        return chain.proceed(request)
    }

    private fun sendLoginBroadcast() {
        AppViewModule.get(App.context()).loginEvent.postValue(Event(401))
    }
}