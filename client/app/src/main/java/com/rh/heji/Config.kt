package com.rh.heji

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
        this.enableOfflineMode = enableOffline
    }

    fun isInitBook() = ::book.isInitialized

    fun isInitUser() = ::user.isInitialized

    fun setBook(book: Book) {
        this.book = book
    }

    fun setUser(user: JWTParse.User) {
        this.user = user
    }

    fun setToken(token: String) {
        this.token = token
    }

    const val localUserName = "LocalUser"

    //离线用户，有且仅有一个
    var localUser = JWTParse.User(localUserName, mutableListOf("Admin"), "")
        private set

    //默认账本离线账本-离线用户可以创建多个账本
    var defaultBook = Book(
        name = "个人账本",
        createUser = this.localUser.name,
        firstBook = true,
        type = "离线账本",
    )
        private set

}