package com.hao.heji.sync.impl

import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.data.db.Book
import com.hao.heji.data.db.STATUS
import com.hao.heji.launchIO
import com.hao.heji.sync.IBookSync
import com.hao.heji.data.repository.BookRepository
import kotlinx.coroutines.CoroutineScope

/**
 * 账本同步实现
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class BookSyncImpl(private val scope: CoroutineScope) : IBookSync {
    val bookRepository = BookRepository()
    override fun compare() {

    }

    override fun getInfo(bookID: String) {
        TODO("Not yet implemented")
    }

    override fun delete(bookID: String) {
        scope.launchIO({
            val response = bookRepository.deleteBook(bookID)
            if (response.success()) {
                App.dataBase.bookDao().deleteById(response.data)
            }
        })
    }

    override fun clearBill(bookID: String) {
        scope.launchIO({
            //TODO 服务端清除账单
            //val response =HejiNetwork.getInstance().bookClear()
        })
    }

    override fun add(book: Book) {
        if (Config.enableOfflineMode) return
        scope.launchIO({
            val response = bookRepository.createBook(book)
            if (response.success()) {
                val newBook = Book(name = book.name).apply {
                    id = book.id
                    syncStatus = STATUS.SYNCED
                    crtUserId = book.crtUserId
                }
                App.dataBase.bookDao().upsert(book = newBook)
            }
        })
    }

    override fun update(book: Book) {
        scope.launchIO({
            val response = bookRepository.updateBook(
                book_id = book.id,
                bookName = book.name,
                bookType = book.type.toString()
            )
            if (response.success()) {
                App.dataBase.bookDao().update(book = book.apply {

                })
            }
        })
    }
}