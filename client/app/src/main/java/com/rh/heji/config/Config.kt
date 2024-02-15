package com.rh.heji.config

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
    createUser = LocalUser.name,
    isInitial = true,
    type = "离线账本",
)

object Config {
    /**
     * last switch book
     */
    lateinit var book: Book
        private set
    var token = ""
        private set

    /**
     * 当前用户
     */
    lateinit var user: JWTParse.User
        private set

    //开启离线使用
    var enableOfflineMode = false
        private set

    //设定使用模式
    fun setUseMode(enableOffline: Boolean) {
        enableOfflineMode = enableOffline
    }

    //已初始化账本
    fun isInitBook() = Config::book.isInitialized

    //已初始化用户
    fun isInitUser() = Config::user.isInitialized

    //设定当前账本
    fun setBook(book: Book) {
        Config.book = book
    }

    //设定当前用户
    fun setUser(user: JWTParse.User) {
        Config.user = user
    }

    fun setToken(token: String) {
        Config.token = token
    }

    //本地用户
    const val localUserName = "LocalUser"

    //离线用户，有且仅有一个
    val localUser = JWTParse.User(localUserName, "user0", "")

    //默认账本离线账本-离线用户可以创建多个账本
    val defaultBook = Book(
        name = "个人账本",
        createUser = localUser.name,
        isInitial = true,
        type = "离线账本",
    )
}