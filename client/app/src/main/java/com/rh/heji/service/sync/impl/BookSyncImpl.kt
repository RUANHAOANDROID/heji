package com.rh.heji.service.sync.impl

import com.rh.heji.App
import com.rh.heji.config.Config
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.launchIO
import com.rh.heji.network.HttpManager
import com.rh.heji.service.sync.IBookSync
import kotlinx.coroutines.CoroutineScope

/**
 * 账本同步实现
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class BookSyncImpl(private val scope: CoroutineScope) : IBookSync {
    override fun compare() {

    }

    override fun getInfo(bookID: String) {
        TODO("Not yet implemented")
    }

    override fun delete(bookID: String) {
        scope.launchIO({
            val response = HttpManager.getInstance().bookDelete(bookID)
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
            val response = HttpManager.getInstance().createBook(book)
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
            val response = HttpManager.getInstance()
                .bookUpdate(
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