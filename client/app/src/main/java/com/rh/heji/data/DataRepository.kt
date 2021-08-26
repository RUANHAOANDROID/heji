package com.rh.heji.data

import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.network.HejiNetwork

/**
 * ·····························
 *      ADD
 *  DATA ADD    -->LOCAL DB
 *  DATA UPLOAD -->SERVER DB
 *  DATA UPDATE -->LOCAL DB -STATUS
 *  ····························
 *      DELETE
 *  DATE DELETE  -->RE_DELETE
 *  DATE DELETE  -->SERVER
 *  DATE DELETE  -->LOCAL DB
 *  ····························
 *      UPDATE
 *  DATE UPDATE  -->UPDATE SEVER
 *  DATE UPDATE  -->UPDATE LOCAL DB STATUS
 *  ····························
 *      QUERY
 *  DATE QUERY  -->LOCAL DB
 *  DATE QUERY  -->SERVER PULL
 *  DATE UPDATE -->UPSERT TO LOCAL DB
 *  ····························
 */
class DataRepository {
    companion object NETWORK {
        const val OK = 0;
    }

    val network = HejiNetwork.getInstance()
    val database = AppDatabase.getInstance()
    val bookDao = database.bookDao()
    val billDao = database.billDao()
    val categoryDao = database.categoryDao()

    suspend fun addBook(book: Book) {
        bookDao.insert(book)
        network.bookUpdate(book.id, book.name, book.type!!).apply {
            if (code == OK) {
                book.synced = STATUS.SYNCED
                bookDao.update(book)
            }
        }
    }
}