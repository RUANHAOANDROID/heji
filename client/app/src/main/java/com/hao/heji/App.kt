package com.hao.heji

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.config.Config
import com.hao.heji.data.AppDatabase
import io.sentry.Sentry
import kotlinx.coroutines.runBlocking
import io.sentry.android.core.SentryAndroid
/**
 * @date: 2020/8/28
 * @author: 锅得铁
 * # 启动初始化顺序：
 *  1.AppInitializer
 *  2.Application
 *  3.AppViewModule
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        SentryAndroid.init(this) { config ->
            config.dsn =
                "https://77565e76a3ff93fa653f67e77a9bc41d@o4508631282155520.ingest.us.sentry.io/4508922877771776"
            config.isDebug = true
        }
        runBlocking {
            Config.load(context)
            LogUtils.d("enableOfflineMode=${Config.enableOfflineMode}", Config.book, Config.user)
        }
        switchDataBase(Config.user.id)
        viewModel = AppViewModel(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        @SuppressLint("StaticFieldLeak")
        lateinit var viewModel: AppViewModel
            private set

        /**
         * 数据库在登录后初始化不同用户创建不同数据库（username_data）
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var dataBase: AppDatabase
            private set

        @JvmName("switchDataBase")
        fun switchDataBase(userName: String) {
            //选择数据库时关闭上一个数据库连接并重建
            if (this::dataBase.isInitialized)
                dataBase.reset()
            dataBase = AppDatabase.getInstance(userName)
        }
    }
}
