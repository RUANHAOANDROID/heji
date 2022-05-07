package com.rh.heji

import android.app.Application
import android.content.Context
import com.rh.heji.utlis.http.basic.HttpRetrofit

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * # 启动初始化顺序：
 *  1.AppInitializer
 *  2.Application
 *  3.AppViewModule
 */
class App : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: App? = null
        fun context(): Context = instance!!.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance =this
        init()
    }


    private fun init() {
    }
}
