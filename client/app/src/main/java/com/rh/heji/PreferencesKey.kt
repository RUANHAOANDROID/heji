package com.rh.heji

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rh.heji.data.db.Book
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * PreferencesKey
 * @date 2022/5/9
 * @author 锅得铁
 * @since v1.0
 */
object PreferencesKey {
    /**
     * 用户当前账本
     */
    val STARTUP_COUNT = intPreferencesKey("startup_count")

    /**
     * JWT_TOKEN
     */
    val JWT_TOKEN = stringPreferencesKey("jwt_token")

    /**
     * 用户当前账本
     */
    val CURRENT_BOOK = stringPreferencesKey("current_book")

    /**
     *当前登录用户
     */
    val CURRENT_USER = stringPreferencesKey("current_user")
}

object DataStoreManager {
    /**
     * 启动次数计数
     *
     */
    suspend fun startupCount(context: Context) {
        context.dataStore.edit {
            val startupCount = it[PreferencesKey.STARTUP_COUNT] ?: 0
            it[PreferencesKey.STARTUP_COUNT] = startupCount + 1
        }
    }

    suspend fun startupCount(): Int {
        return App.context.dataStore.data.map {
            it[PreferencesKey.STARTUP_COUNT] ?: 1
        }.first()
    }

    /**
     * Save token
     *
     * @param token
     */
    suspend fun saveToken(token: String) {
        App.context.dataStore.edit {
            it[PreferencesKey.JWT_TOKEN] = token
        }
    }

    /**
     * Save token
     *
     * @param token
     */
    suspend fun deleteToken() {
        App.context.dataStore.edit {
            it.remove(PreferencesKey.JWT_TOKEN)
        }
    }

    /**
     * 获取TOKEN,可能Token不存在
     *
     * @return
     */
    suspend fun getToken(): Flow<String?> {
        return App.context.dataStore.data.map { it[PreferencesKey.JWT_TOKEN] }
    }

    suspend fun saveCurrentBook(book: Book) {
        App.context.dataStore.edit {
            it[PreferencesKey.CURRENT_BOOK] = moshi.adapter(Book::class.java).toJson(book)
        }
    }

    suspend fun getCurrentBook(): Flow<Book?> {
        return App.context.dataStore.data.map {
            var currentBook: Book? = null
            val bookJsonString = it[PreferencesKey.CURRENT_BOOK]
            if (!bookJsonString.isNullOrEmpty()) {
                currentBook = moshi.adapter<Book>(Book::class.java).fromJson(bookJsonString)
            }
            currentBook
        }
    }

}