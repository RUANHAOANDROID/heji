package com.rh.heji.utlis.http.basic;


import com.rh.heji.BuildConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * # 为多模块、模块对应不同微服务设计。各模块通过该类创建Service
 */
public class ServiceCreator<T> {

    private static ServiceCreator INSTANCE = null;
    private Map<String ,T> servers = new LinkedHashMap<>();

    public static ServiceCreator getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new ServiceCreator();
        }

        return INSTANCE;
    }
    /**
     * 初始化请求接口服务
     *
     * @param clazz 服务类
     * @param url   服务地址信息
     */
    public T createService(Class<T> clazz, String url) {
        if (servers.containsKey(clazz.getName())){
            return servers.get(clazz.getName());
        }
        T server = HttpRetrofit.create(url, clazz);
        servers.put(clazz.getName(),server);
        return server;
    }

    /**
     * 使用默认配置URL初始化
     *
     * @param clazz 服务类
     */
    public T createService(Class<T> clazz) {
        if (servers.containsKey(clazz.getName())){
            return servers.get(clazz.getName());
        }
        T server = HttpRetrofit.create(BuildConfig.HTTP_URL, clazz);
        servers.put(clazz.getName(),server);
        return server;
    }

}
