package com.rh.heji.utlis.http.basic

import com.rh.heji.BuildConfig
import com.rh.heji.moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * -Retrofit实例化
 */
object HttpRetrofit {


    /**
     * 初始化OkHttpClient
     * @param client
     */
    fun okHttpClient(
        connectTimeout: Long = 10,
        readTimeout: Long = 10,
        writeTimeout: Long = 10,
        bufferSize: Long = 1028 * 8,
        servletDownloadUserHeadImg: String = ""
    ): OkHttpClient {


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
            .build()
    }

    /**
     * 创建服务实例
     * @param url 服务地址
     * @param service 服务接口
     * @param <T>
     * @return 返回服务实例
    </T> */
    fun <T> create(url: String?, service: Class<T>?): T {
//        Gson gson =new GsonBuilder().serializeNulls()
//                .setDateFormat("yyyy-mm-dd HH:mm:ss")
//                .create();
        val rt = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            //.addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        return rt.create(service)
    }
}