package com.rh.heji

import android.content.Context
import androidx.startup.Initializer
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.tencent.mmkv.MMKV

/**
 * startup.InitializationProvider
 */
class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        MMKV.initialize(context)
        AppDatabase.getInstance(context).bookDao().createNewBook(Book(name = "AppInitalizer"))
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}