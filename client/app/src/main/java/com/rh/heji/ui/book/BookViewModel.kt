package com.rh.heji.ui.book

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.currentUser
import com.rh.heji.App
import com.rh.heji.data.Result
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.BookUser
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.BookRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO
import com.rh.heji.utlis.runMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookViewModel : BaseViewModel() {
    private val bookLiveData = MediatorLiveData<Book>()
    private val bookListLiveData = MediatorLiveData<MutableList<Book>>()
    private val bookDao = App.dataBase.bookDao()
    private val bookRepository=BookRepository()
    fun createNewBook(name: String, type: String): LiveData<Book> {

        launchIO({
            bookRepository.addBook(Book(name = name,type=type))
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
                HejiNetwork.getInstance().bookCreate(book)
            }
        }, {})

        //netwook create
        return bookLiveData
    }

    fun isFirstBook(id: String) = App.dataBase.bookDao().isFirstBook(id)

    fun countBook(book_id: String): Int {
        return App.dataBase.billDao().countByBookId(book_id)
    }

    fun getBookList(): LiveData<MutableList<Book>> {
        launchIO({
            val allBooks = bookDao.allBooks()
            bookListLiveData.postValue(allBooks)
            val response = HejiNetwork.getInstance().bookPull()
            val netBooks = response.data
            if (netBooks.isNotEmpty()) {
                bookListLiveData.postValue(netBooks)
                for (book in netBooks) {
                    book.synced = STATUS.SYNCED
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
            if (response.data.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    call(response.data)
                }
            }
        })

    }

    fun clearBook(id: String, call: (Result<String>) -> Unit) {
        launchIO({
            App.dataBase.billDao().deleteByBookId(id)
            call(Result.Success("清楚账单成功"))
        }, { call(Result.Error(it)) })
    }

    fun deleteBook(id: String, call: (Result<String>) -> Unit) {
        launchIO({
            val billsCount = App.dataBase.billDao().countByBookId(id)
            if (billsCount > 0) {
                ToastUtils.showLong("该账本下存在账单，无法直接删除")
            } else {
                val response = HejiNetwork.getInstance().bookDelete(id)
                if (response.code == 0) {
                    App.dataBase.bookDao().deleteById(id)
                    runMainThread {
                        call(Result.Success("删除成功"))
                    }
                }
            }
        }, {
            call(Result.Error(it))
        })
    }

    fun sharedBook(bookId: String, @MainThread call: (Result<String>) -> Unit) {
        launchIO({
            val response = HejiNetwork.getInstance().bookShared(book_id = bookId)
            if (response.data.isNotEmpty()) {
                val shareCode = response.data as String
                runMainThread {
                    call(Result.Success(shareCode))
                }
            }
        }, {
            call(Result.Error(it))
        })
    }

    fun joinBook(code: String, call: (Result<String>) -> Unit) {
        launch({
            call(Result.Loading)
            val response = HejiNetwork.getInstance().bookJoin(code)
            call(Result.Success(response.data))
        }, { call(Result.Error(it)) })

    }
}