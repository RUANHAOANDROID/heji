package com.rh.heji.service.sync

import com.rh.heji.App
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.launchIO
import com.rh.heji.network.HejiNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 账本同步实现
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class BookSyncImpl(private val scope: CoroutineScope) : IBookSync {
    override fun compare() {

    }

    override fun delete(bookID: String) {
        scope.launchIO({
            val response = HejiNetwork.getInstance().bookDelete(bookID)
            if (response.success()) {
                App.dataBase.bookDao().deleteById(response.data)
            }
        })
    }

    override fun add(book: Book) {
        scope.launchIO({
            val response = HejiNetwork.getInstance().bookCreate(book)
            if (response.success()) {
                val newBook = Book(name = book.name).apply {
                    id = book.id
                    synced = STATUS.SYNCED
                    createUser = book.createUser
                }
                App.dataBase.bookDao().upsert(book = newBook)
            }
        })
    }

    override fun update(book: Book) {
        scope.launchIO({
            val response = HejiNetwork.getInstance()
                .bookUpdate(
                    book_id = book.id,
                    bookName = book.name,
                    bookType = book.type.toString()
                )
            if (response.success()) {
                App.dataBase.bookDao().update(book = book.apply {
                    TODO()
                })
            }
        })
    }
}