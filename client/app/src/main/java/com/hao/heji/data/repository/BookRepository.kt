package com.hao.heji.data.repository

import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.data.db.Book
import com.hao.heji.network.HttpManager

class BookRepository {
    suspend fun findBook(bid: String) = HttpManager.getInstance().findBook(bid)
    suspend fun createBook(book: Book) = App.dataBase.bookDao().insert(book)

    suspend fun bookList() = HttpManager.getInstance().bookList()
    suspend fun sharedBook(bid: String) = HttpManager.getInstance().sharedBook(bid)
    suspend fun deleteBook(bid: String) = HttpManager.getInstance().deleteBook(bid)
    suspend fun updateBook(bid: String, bookName: String, bookType: String) =
        HttpManager.getInstance().updateBook(bid, bookName, bookType)

    suspend fun joinBook(sharedCode: String) = HttpManager.getInstance().joinBook(sharedCode)
}