package com.hao.heji.config

import android.content.Context
import com.hao.heji.BuildConfig
import com.hao.heji.config.store.DataStoreManager
import com.hao.heji.data.db.Book
import com.hao.heji.ui.user.JWTParse
import kotlinx.coroutines.flow.firstOrNull

/**
 *Date: 2022/11/13
 *Author: 锅得铁
 *#
 */
internal val LocalUser = JWTParse.User("LocalUser", "user0", "")
internal val InitBook = Book(
    name = "个人账本",
    crtUserId = LocalUser.id,
    isInitial = true,
    type = "离线账本",
)

object Config {

    var serverUrl = BuildConfig.HTTP_URL

    /**
     * last switch book
     */
    var book: Book = InitBook

    /**
     * 当前用户
     */
    var user = LocalUser

    /**
     * 开启离线使用模式
     */
    var enableOfflineMode = false

    fun isInitUser() = (user == LocalUser)

    suspend fun saveBook(book: Book) {
        this.book = book
        DataStoreManager.saveBook(book)
    }

    suspend fun saveUser(user: JWTParse.User) {
        this.user = user
        DataStoreManager.saveToken(user.token)
    }

    suspend fun saveOfflineMode(enable: Boolean) {
        enableOfflineMode = enable
        DataStoreManager.saveUseMode(enableOfflineMode)
    }

    suspend fun load(context: Context) {
        with(DataStoreManager) {
            getUseMode(context).firstOrNull()?.let { enableOfflineMode = it }
            if (!enableOfflineMode) {
                getBook(context).firstOrNull()?.let { book = it }
                getToken(context).firstOrNull()?.let { user = JWTParse.getUser(jwt = it) }
            } else {
                user = LocalUser
                book = InitBook
            }
        }
    }

    suspend fun save() {
        with(DataStoreManager) {
            saveUseMode(false)
            saveBook(book)
            saveToken(user.token)
        }
    }

    suspend fun remove() {
        with(DataStoreManager) {
            removeUseMode()
            removeToken()
            removeBook()
        }
        enableOfflineMode = false
        user = LocalUser
        book = InitBook
    }
}