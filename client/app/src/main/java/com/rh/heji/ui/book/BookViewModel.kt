package com.rh.heji.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.AppViewModule
import com.rh.heji.currentUser
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO

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
            bookListLiveData.postValue(bookDao.allBooks())
        }, {})
        return bookListLiveData
    }
}