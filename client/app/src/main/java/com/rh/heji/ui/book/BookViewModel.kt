package com.rh.heji.ui.book

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.AppViewModule
import com.rh.heji.currentUser
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.BookUser
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.network.HeJiServer
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO
import com.rh.heji.utlis.runMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookViewModel : BaseViewModel() {
    private val bookLiveData = MediatorLiveData<Book>()
    private val bookListLiveData = MediatorLiveData<MutableList<Book>>()
    private val bookDao = AppDatabase.getInstance().bookDao()
    fun createNewBook(name: String, type: String): LiveData<Book> {

        launchIO({
            val count = bookDao.countByName(name)
            if (count > 0) {
                ToastUtils.showLong("账本名已经存在")
            } else {
                val book = Book(
                    id = ObjectId().toHexString(),
                    name = name,
                    type = type,
                    createUser = currentUser.username
                )
                bookDao.insert(book)
                bookLiveData.postValue(book)
                HejiNetwork.getInstance().bookPush(book)
            }
        }, {})

        //netwook create
        return bookLiveData
    }

    fun getBookList(): LiveData<MutableList<Book>> {
        launchIO({
            val allBooks = bookDao.allBooks()
            bookListLiveData.postValue(allBooks)
            val response = HejiNetwork.getInstance().bookPull()
            val netBooks = response.date
            if (netBooks.isNotEmpty()) {
                bookListLiveData.postValue(netBooks)
                for (book in netBooks) {
                    if (bookDao.exist(book.id) > 0) {
                        bookDao.update(book)
                    } else {
                        bookDao.insert(book)
                    }
                }
            }
        }, {})
        return bookListLiveData
    }

    fun getBookUsers(bookId: String, @MainThread call: (MutableList<BookUser>) -> Unit) {
        launchIO({
            val response = HejiNetwork.getInstance().bookGetUsers(bookId)
            if (response.date.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    call(response.date)
                }
            }
        })

    }

    fun addBookUser(bookId: String, @MainThread call: (String) -> Unit) {
        launchIO({
            val response = HejiNetwork.getInstance().bookAddUser(book_id = bookId)
            if (response.date.isNotEmpty()) {
                val shareCode = response.date as String
                runMainThread {
                    call(shareCode)
                }
            }
        })
    }
}