package com.rh.heji.ui.book

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.Config
import com.rh.heji.data.Result
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.BookUser
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.BookRepository
import com.rh.heji.network.HttpManager
import com.rh.heji.service.sync.IBookSync
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO
import com.rh.heji.utlis.runMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class BookViewModel(private val mBookSync: IBookSync) : ViewModel() {
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
                bookRepository.addBook(Book(name = name, type = type))
                val book = Book(
                    id = ObjectId().toHexString(),
                    name = name,
                    type = type,
                    createUser = Config.user.name
                )
                mBookSync.add(book)
                _bookLiveData.postValue(book)
            }
        })

        //network create
    }

    fun isFirstBook(id: String) = App.dataBase.bookDao().isFirstBook(id)

    fun countBook(book_id: String): Int {
        return App.dataBase.billDao().countByBookId(book_id)
    }

    fun bookList(): LiveData<MutableList<Book>> {
        return _bookListLiveData
    }

    fun getBookList() {
        launchIO({
            val response = HttpManager.getInstance().bookPull()
            val netBooks = response.data
            if (netBooks.isNotEmpty()) {
                for (book in netBooks) {
                    book.syncStatus = STATUS.SYNCED
                    if (bookDao.exist(book.id) > 0) {
                        bookDao.update(book)
                    } else {
                        bookDao.insert(book)
                    }
                }
            } else {
                ToastUtils.showLong("没有更多账本")
            }
        })
    }

    fun getBookUsers(bookId: String, @MainThread call: (MutableList<BookUser>) -> Unit) {
        launchIO({
            val response = HttpManager.getInstance().bookGetUsers(bookId)
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
                val response = HttpManager.getInstance().bookDelete(id)
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
            val response = HttpManager.getInstance().bookShared(book_id = bookId)
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
            val response = HttpManager.getInstance().bookJoin(code)
            call(Result.Success(response.data))
        }, { call(Result.Error(it)) })

    }
}