package com.rh.heji.data

import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.network.HejiNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * ·····························
 *      ADD
 *  DATA ADD    -->LOCAL DB
 *  DATA UPLOAD -->SERVER DB
 *  DATA UPDATE -->LOCAL DB -STATUS
 *  ····························
 *      DELETE
 *  DATE DELETE  -->LOCAL DB RE_DELETE
 *  DATE DELETE  -->SERVER DELETE
 *  DATE DELETE  -->LOCAL DB DELETE
 *  ····························
 *      UPDATE
 *  DATE UPDATE  -->UPDATE LOCAL DB
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

    private val network = HejiNetwork.getInstance()
    private val database = AppDatabase.getInstance()
    private val bookDao = database.bookDao()
    private val billDao = database.billDao()
    private val categoryDao = database.categoryDao()

    suspend fun addBook(book: Book) {
        bookDao.insert(book)
        network.bookUpdate(book.id, book.name, book.type!!).apply {
            if (code == OK) {
                book.synced = STATUS.SYNCED
                bookDao.update(book)
            }
        }
    }

    suspend fun deleteBoo(book_id: String) {
        bookDao.preDelete(book_id)
        network.bookDelete(book_id).apply {
            if (code == OK) {
                bookDao.deleteById(book_id);
            }
        }
    }

    suspend fun updateBook(book: Book) {
        book.synced = STATUS.UPDATED
        bookDao.update(book)
        network.bookUpdate(book.id, book.name, book.type!!).apply {
            if (code == OK) {
                book.synced = STATUS.SYNCED
                bookDao.update(book)
            }
        }
    }

    suspend fun queryBook(): Flow<MutableList<Book>> {
        return flow<MutableList<Book>> {
            emit(bookDao.allBooks())
            network.bookPull().apply {
                if (code == OK && data.isNotEmpty()) {
                    emit(data)
                    data.forEach {
                        bookDao.upsert(it)
                    }
                }
            }
        }
    }
}