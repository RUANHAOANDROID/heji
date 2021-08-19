package com.rh.heji

import android.content.Context
import androidx.startup.Initializer
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.tencent.mmkv.MMKV

/**
 * startup.InitializationProvider
 */
class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        LogUtils.d("AppInitializer", "MMK")
        MMKV.initialize(context)
        LogUtils.d("AppInitializer", "AppDatabase")
        startCount()
        initBook(context)
    }

    private fun initBook(context: Context) {
        AppDatabase.getInstance(context).let {
            val bookDao = it.bookDao()
            if (it.bookDao().count() == 0) {
                bookDao.createNewBook(
                    Book(
                        id = "0",
                        name = "个人账本",
                        createUser = "local",
                        type = "日常账本"
                    )
                )
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    /**
     * 统计启动次数
     */
    private fun startCount() {
        val key = "start"
        val startCount = App.instance.mmkv!!.decodeInt(key, 0)
        LogUtils.d(startCount)
        App.instance.mmkv?.encode(key, startCount + 1)
    }
}