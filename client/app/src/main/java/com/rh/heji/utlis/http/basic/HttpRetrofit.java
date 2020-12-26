package com.rh.heji.utlis.http.basic;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * -Retrofit实例化
 */
public class HttpRetrofit {

    private static OkHttpClient sClient;

    /**
     * 初始化OkHttpClient
     * @param client
     */
    public static void initClient(OkHttpClient client) {
        sClient = client;
    }

    /**
     * 创建服务实例
     * @param url 服务地址
     * @param service 服务接口
     * @param <T>
     * @return 返回服务实例
     */
    public static <T> T create(String url, final Class<T> service) {
        Gson gson =new GsonBuilder().serializeNulls()
                .setDateFormat("yyyy-mm-dd HH:mm:ss")
                .create();
        Retrofit rt = new Retrofit.Builder()
                .baseUrl(url)
                .client(sClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return rt.create(service);
    }


}
