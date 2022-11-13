package com.rh.heji

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.rh.heji.service.sync.SyncService
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.ui.user.security.UserToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
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
        init()
    }

    private fun init() {
        Intent(this, SyncService::class.java).also {
            startService(it)
        }
        runBlocking {
            if (DataStoreManager.startupCount() == 1) {
                //第一次启动
            } else {
                //非首次启动
            }
        }
        val lastUserToken = runBlocking { UserToken.getToken().first() }
        if (lastUserToken != null) {
            user = JWTParse.getUser(lastUserToken)
            setDataBase(user.name)
        }
        val lastBook = runBlocking { DataStoreManager.getCurrentBook().first() }
        if (lastBook != null) {
            currentBook = lastBook
        }

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        @SuppressLint("StaticFieldLeak")
        lateinit var currentBook: Book
            private set

        /**
         * 登录过则取最后一个用户
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var user: JWTParse.User
            private set

        fun userIsInit() = this::user.isInitialized
        fun bookIsInit() = this::currentBook.isInitialized

        @JvmName("switchUser")
        fun setUser(currentUser: JWTParse.User) {
            user = currentUser
        }

        /**
         * 数据库在登录后初始化不同用户创建不同数据库（username_data）
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var dataBase: AppDatabase
            private set

        @JvmName("switchCurrentBook")
        fun setCurrentBook(book: Book) {
            currentBook = book
            runBlocking { DataStoreManager.saveCurrentBook(book) }
            LogUtils.d(currentBook)
        }

        @JvmName("switchDataBase")
        fun setDataBase(userName: String) {
            //选择数据库时关闭上一个数据库连接并重建
            if (this::dataBase.isInitialized)
                dataBase.reset()
            dataBase = AppDatabase.getInstance(userName)
        }

        fun reset() {

        }
    }


}
