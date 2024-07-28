package com.hao.heji.sync.impl

import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.data.db.Book
import com.hao.heji.launchIO
import com.hao.heji.data.repository.BookRepository
import kotlinx.coroutines.CoroutineScope

/**
 * 账本同步实现
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class BookSyncImpl(private val scope: CoroutineScope)  {
    val bookRepository = BookRepository()
    fun compare() {

    }

    fun getInfo(bid: String) {
        TODO("Not yet implemented")
    }

    fun delete(bid: String) {
        scope.launchIO({
            val response = bookRepository.deleteBook(bid)
            if (response.success()) {
                response.data?.let {
                    App.dataBase.bookDao().deleteById(it)
                }
            }
        })
    }

    fun clearBill(bid: String) {
        scope.launchIO({
            //TODO 服务端清除账单
            //val response =HejiNetwork.getInstance().bookClear()
        })
    }

    fun add(book: Book) {
        if (Config.enableOfflineMode) return
    }

    fun update(book: Book) {
        scope.launchIO({
            val response = bookRepository.updateBook(
                bid = book.id,
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