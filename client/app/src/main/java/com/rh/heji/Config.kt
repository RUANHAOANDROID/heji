package com.rh.heji

import com.rh.heji.data.db.Book
import com.rh.heji.data.db.BookUser
import com.rh.heji.store.DataStoreManager
import com.rh.heji.ui.user.JWTParse
import kotlinx.coroutines.runBlocking

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

    fun isInitBook() = this::book.isInitialized

    fun isInitUser() = this::user.isInitialized

    fun setBook(book: Book) {
        this.book = book
    }

    fun setUser(user: JWTParse.User) {
        this.user = user
    }

    fun setToken(token: String) {
        this.token = token
    }

    //离线用户，有且仅有一个
    var localUser = JWTParse.User("LocalUser", mutableListOf("Admin"), "")
        private set

    //默认账本离线账本-离线用户可以创建多个账本
    var defaultBook = Book(
        id = "0",
        name = "个人账本",
        createUser = this.localUser.name,
        firstBook = 0,
        type = "离线账本",
    )
        private set

}