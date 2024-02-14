package com.rh.heji.config

import com.rh.heji.data.db.Book
import com.rh.heji.ui.user.JWTParse

/**
 *Date: 2022/11/13
 *Author: 锅得铁
 *#
 */
object Config {
    /**
     * last switch book
     */
    lateinit var book: Book
        private set

     var token = ""
        private set

    /**
     * last login user
     */
    lateinit var user: JWTParse.User
        private set

    var enableOfflineMode = false
        private set

    fun setUseMode(enableOffline: Boolean) {
        enableOfflineMode = enableOffline
    }

    fun isInitBook() = Config::book.isInitialized

    fun isInitUser() = Config::user.isInitialized

    fun setBook(book: Book) {
        Config.book = book
    }

    fun setUser(user: JWTParse.User) {
        Config.user = user
    }

    fun setToken(token: String) {
        Config.token = token
    }

    const val localUserName = "LocalUser"

    //离线用户，有且仅有一个
    val localUser = JWTParse.User(localUserName, "user0", "")

    //默认账本离线账本-离线用户可以创建多个账本
    val defaultBook = Book(
        name = "个人账本",
        createUser = localUser.name,
        firstBook = true,
        type = "离线账本",
    )
}