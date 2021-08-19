package com.rh.heji.utlis.http.basic

import android.text.TextUtils
import android.util.Log
import com.rh.heji.App
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Header 拦截器
 */
class HttpHeaderInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var bearerToken: String = App.instance.token.decodeToken()
        Log.d("OKHTTP", "HttpHeaderInterceptor $bearerToken")
        if (TextUtils.isEmpty(bearerToken)) {
            Log.d("OKHTTP", "HttpHeaderInterceptor:{not login}")
        }
        val request: Request = chain.request().newBuilder()
            //.header("Content-Type", "application/json")
            .addHeader("Authorization", bearerToken)
            .build()
        return chain.proceed(request)
    }
}