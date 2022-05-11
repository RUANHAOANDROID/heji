package com.rh.heji

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.ui.user.security.UserToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking

/**
 * Date: 2020/8/28
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
        }

        @JvmName("switchDataBase")
        fun setDataBase(userName: String) {
            dataBase = AppDatabase.getInstance(userName)
        }
    }


    private fun init() {

        val lastUserToken = runBlocking { UserToken.getToken().first() }
        if (lastUserToken != null) {
            user = JWTParse.getUser(lastUserToken)
        }
        val lastBook = runBlocking { DataStoreManager.getCurrentBook().first() }
        if (lastBook != null) {
            currentBook = lastBook
        }
    }

}
