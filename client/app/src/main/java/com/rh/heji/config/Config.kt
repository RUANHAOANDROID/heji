package com.rh.heji.config

import com.rh.heji.config.store.DataStoreManager
import com.rh.heji.data.db.Book
import com.rh.heji.ui.user.JWTParse

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

    suspend fun save() {
        with(DataStoreManager) {
            saveUseMode(false)
            saveBook(book)
            saveToken(user.token)
        }
    }
}