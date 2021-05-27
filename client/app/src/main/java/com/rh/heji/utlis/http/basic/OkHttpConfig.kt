package com.rh.heji.utlis.http.basic

import com.rh.heji.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * - OkHttp请求配置
 */
object OkHttpConfig {
    var SERVLET_DOWNLOAD_USER_HEADIMG = ""
    const val BUFFER_SIZE = 64 * 1024
    var connectTimeout: Long = 3 * 15
    var readTimeout: Long = 3 * 15
    var writeTimeout: Long = 3 * 15

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
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
        }

}