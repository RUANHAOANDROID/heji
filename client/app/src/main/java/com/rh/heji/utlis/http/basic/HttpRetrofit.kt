package com.rh.heji.utlis.http.basic

import com.rh.heji.moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * -Retrofit实例化
 */
object HttpRetrofit {
    private var sClient: OkHttpClient? = null

    /**
     * 初始化OkHttpClient
     * @param client
     */
    fun initClient(client: OkHttpClient?) {
        sClient = client
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
            .client(sClient)
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            //.addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        return rt.create(service)
    }
}