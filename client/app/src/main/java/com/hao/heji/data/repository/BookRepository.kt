package com.hao.heji.data.repository

import com.hao.heji.App
import com.hao.heji.data.db.Book
import com.hao.heji.network.HttpManager
import retrofit2.await

class BookRepository {
    private val bookDao = App.dataBase.bookDao()
    private val server = HttpManager.getInstance().server()

    suspend fun findBook(book_id: String) = server.findBook(book_id).await()
    suspend fun createBook(book: Book) = server.createBook(book).await()
    suspend fun bookList() = server.bookList().await()
    suspend fun sharedBook(book_id: String) = server.sharedBook(book_id).await()
    suspend fun deleteBook(book_id: String) = server.deleteBook(book_id).await()
    suspend fun updateBook(book_id: String, bookName: String, bookType: String) =
        server.updateBook(book_id, bookName, bookType).await()

    suspend fun joinBook(sharedCode: String) = server.joinBook(sharedCode).await()

    suspend fun addBook(book: Book) {
        bookDao.insert(book)
    }
}