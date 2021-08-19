package com.rh.heji.utlis.http.basic

import android.text.TextUtils
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Header 拦截器
 */
class HttpHeaderInterceptor(val bearerToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("OkHttpConfig", bearerToken)
        if (TextUtils.isEmpty(bearerToken)) {
            Log.d("OkHttpConfig", "not login")
        }
        val request: Request = chain.request().newBuilder()
            //.header("Content-Type", "application/json")
            .addHeader("Authorization", bearerToken)
            .build()
        return chain.proceed(request)
    }
}