package com.rh.heji.utlis.http.basic

import android.text.TextUtils
import android.util.Log
import com.rh.heji.AppCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Header 拦截器
 */
 class HttpHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("OkHttpConfig", "add log interceptor")
        val bearerToken = runBlocking(Dispatchers.IO) {
            Log.d("OkHttpConfig", "get token")
            AppCache.getInstance().token.tokenString
        }
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