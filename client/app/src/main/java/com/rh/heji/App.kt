package com.rh.heji

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * # 启动初始化顺序：
 *  1.AppInitializer
 *  2.Application
 *  3.AppViewModule
 */
class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        init()
    }


    override fun onTerminate() {
        super.onTerminate()
    }

    private fun init() {
    }
}
