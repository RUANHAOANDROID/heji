package com.rh.heji

import com.rh.heji.data.db.Book
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

    /**
     * last login user
     */
    lateinit var user: JWTParse.User
        private set

    fun isInitBook() = this::book.isInitialized

    fun isInitUser() = this::user.isInitialized

    fun setBook(book: Book) {
        this.book = book
        runBlocking { DataStoreManager.saveCurrentBook(book) }
    }

    fun setUser(user: JWTParse.User) {
        this.user = user
    }
}