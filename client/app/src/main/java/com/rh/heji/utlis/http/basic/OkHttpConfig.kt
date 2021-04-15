package com.rh.heji.utlis.http.basic

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.App
import com.rh.heji.AppCache
import com.rh.heji.BuildConfig
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.wait
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * - OkHttp请求配置
 */
object OkHttpConfig {
    var SERVLET_DOWNLOAD_USER_HEADIMG = ""
    const val BUFFER_SIZE = 64 * 1024
    var connectTimeout = 10000L
    var readTimeout = 10000L
    var writeTimeout = 10000L

    /**
     * OkHttpClient 配置
     *
     * @return
     */
    @JvmStatic
    val clientBuilder: OkHttpClient.Builder
        get() {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            if (!BuildConfig.DEBUG) {
                logging.redactHeader("Authorization")
                logging.redactHeader("Cookie")
            }
            val headerInterceptor = HttpHeaderInterceptor()
            return OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        }

    /**
     * Header 拦截器
     */
    internal class HttpHeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            Log.d("OkHttpConfig", "add log interceptor")
            val bearerToken = runBlocking(Dispatchers.IO) {
                Log.d("OkHttpConfig", "get token")
                AppCache.instance.token.get()
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
}