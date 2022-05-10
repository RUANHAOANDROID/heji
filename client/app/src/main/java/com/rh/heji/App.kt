package com.rh.heji

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.mongo.ObjectId
import kotlinx.coroutines.flow.first
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
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        lateinit var currentBook: Book
            private set

        @JvmName("setCurrentBook1")
        fun setCurrentBook(book: Book) {
            currentBook = book
            runBlocking { DataStoreManager.saveCurrentBook(book) }
        }

    }

    override fun onCreate() {
        super.onCreate()
        context = this
        init()
    }


    private fun init() {
        /**
         * 当前账本
         */
        currentBook =
            runBlocking { DataStoreManager.getCurrentBook().first() ?: Book(name = "个人账本") }
    }

}
