package com.rh.heji

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.config.Config
import com.rh.heji.config.InitBook
import com.rh.heji.config.LocalUser
import com.rh.heji.data.AppDatabase
import com.rh.heji.service.sync.SyncService
import com.rh.heji.config.store.DataStoreManager
import com.rh.heji.ui.user.JWTParse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

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
        runBlocking {
            with(DataStoreManager) {
                getUseMode(context).first()?.let {
                    Config.enableOfflineMode = it
                }
                if (Config.enableOfflineMode) {
                    Config.user = LocalUser
                    Config.book = InitBook
                } else {
                    getToken(context).first()?.let {
                        Config.user = JWTParse.getUser(it)
                    }
                }
                getBook(context).first()?.let {
                    Config.book = it
                    LogUtils.d(it)
                }
            }
            LogUtils.d("enableOfflineMode=${Config.enableOfflineMode}", Config.book, Config.user)
        }
        switchDataBase(Config.user.id)
        viewModel = AppViewModel(this)
        Intent(this, SyncService::class.java).also {
            startService(it)
        }
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
