package com.hao.heji.ui.book

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.ToastUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.data.Result
import com.hao.heji.data.db.Book
import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.data.repository.BookRepository
import com.hao.heji.utils.launch
import com.hao.heji.utils.launchIO
import com.hao.heji.utils.runMainThread
import kotlinx.coroutines.flow.*

class BookViewModel : ViewModel() {
    private val _bookLiveData = MediatorLiveData<Book>()
    private val _bookListLiveData = MediatorLiveData<MutableList<Book>>()
    private val bookDao = App.dataBase.bookDao()

    private val bookRepository = BookRepository()
    private val booksFlow = App.dataBase.bookDao().allBooks()

    init {
        launchIO({
            booksFlow.filterNotNull().collect {
                _bookListLiveData.postValue(it)
            }
        })

    }


    fun bookCreate(): LiveData<Book> {
        return _bookLiveData
    }

    fun createNewBook(name: String, type: String) {

        launchIO({
            val count = bookDao.countByName(name)
            if (count > 0) {
                ToastUtils.showLong("账本名已经存在")
            } else {
                bookRepository.createBook(Book(name = name, type = type))
                val book = Book(
                    id = ObjectId().toHexString(),
                    name = name,
                    type = type,
                    crtUserId = Config.user.id
                )
                _bookLiveData.postValue(book)
            }
        })

        //network create
    }

    fun isFirstBook(id: String) = App.dataBase.bookDao().isInitialBook(id)

    fun countBook(book_id: String): Int {
        return App.dataBase.billDao().countByBookId(book_id)
    }

    fun bookList(): LiveData<MutableList<Book>> {
        return _bookListLiveData
    }

    fun getBookList() {
        launch({
            val response = bookRepository.bookList()
            response.data?.let {
                if (it.isNullOrEmpty()) {
                    ToastUtils.showLong("没有更多账本")
                    return@launch
                }
                for (book in it) {
                    book.synced=1
                    if (bookDao.exist(book.id) > 0) {
                        bookDao.update(book)
                    } else {
                        bookDao.insert(book)
                    }
                }
            }

        })
    }

    fun clearBook(id: String, call: (Result<String>) -> Unit) {
        launchIO({
            App.dataBase.billDao().deleteByBookId(id)
            call(Result.Success("清除账单成功"))
        }, { call(Result.Error(it)) })
    }

    fun deleteBook(id: String, call: (Result<String>) -> Unit) {
        launchIO({
            val billsCount = App.dataBase.billDao().countByBookId(id)
            if (billsCount > 0) {
                ToastUtils.showLong("该账本下存在账单，无法直接删除")
            } else {
                App.dataBase.bookDao().preDelete(id)
                runMainThread {
                    call(Result.Success("删除成功"))
                }
            }
        }, {
            call(Result.Error(it))
        })
    }

    fun sharedBook(bookId: String, @MainThread call: (Result<String>) -> Unit) {
        launchIO({
            val response = bookRepository.sharedBook(bid = bookId)
            response.data?.let {
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
            val response = bookRepository.joinBook(code)
            response.data?.let {
                call(Result.Success(it))
            }

        }, { call(Result.Error(it)) })

    }
}