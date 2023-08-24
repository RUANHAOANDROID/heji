package com.rh.heji.store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rh.heji.App
import com.rh.heji.data.db.Book
import com.rh.heji.dataStore
import com.rh.heji.moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStoreManager
 * @date 2022/5/9
 * @author 锅得铁
 * @since v1.0
 */
object DataStoreManager {
    /**
     * 当前登录用户凭证
     */
    private val JWT_TOKEN = stringPreferencesKey("jwt_token")

    /**
     * 当前登录用户凭证
     */
    private val USE_MODE = booleanPreferencesKey("use_mode")

    /**
     * 用户当前账本
     */
    private val CURRENT_BOOK = stringPreferencesKey("current_book")


    suspend fun saveUseMode(enableOffline: Boolean, context: Context = App.context) {
        context.dataStore.edit {
            it[USE_MODE] = enableOffline
        }
    }

    fun getUseMode(context: Context = App.context): Flow<Boolean?> {
        return context.dataStore.data.map { it[USE_MODE] }
    }

    suspend fun removeUseMode(context: Context = App.context) {
        context.dataStore.edit {
            it.remove(USE_MODE)
        }
    }

    suspend fun saveToken(token: String, context: Context = App.context) {
        context.dataStore.edit {
            it[JWT_TOKEN] = token
        }
    }

    suspend fun getToken(context: Context = App.context): Flow<String?> {
        return context.dataStore.data.map { it[JWT_TOKEN] }
    }

    suspend fun removeToken(context: Context = App.context) {
        context.dataStore.edit {
            it.remove(JWT_TOKEN)
        }
    }


    suspend fun saveBook(book: Book, context: Context = App.context) {
        context.dataStore.edit {
            it[CURRENT_BOOK] = moshi.adapter(Book::class.java).toJson(book)
        }
    }

    suspend fun getBook(context: Context = App.context): Flow<Book?> = context.dataStore.data.map { preferences ->
        val bookJsonStr = preferences[CURRENT_BOOK]
        bookJsonStr?.let {
            moshi.adapter(Book::class.java).fromJson(bookJsonStr)
        }
    }

    suspend fun removeBook(context: Context = App.context) {
        context.dataStore.edit {
            it.remove(CURRENT_BOOK)
        }
    }

}