package com.rh.heji.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO

class BookViewModel : BaseViewModel() {
    private val bookLiveData = MediatorLiveData<Book>()
    private val bookListLiveData = MediatorLiveData<MutableList<Book>>()

    fun createNewBook(name: String, type: String): LiveData<Book> {
        launchIO({
            val count = AppDatabase.getInstance().bookDao().countByName(name)
            if (count > 0) {
                ToastUtils.showLong("账本名已经存在")
            } else {
                val book = Book(
                    id = ObjectId().toHexString(),
                    name = name,
                    type = type,
                    createUser = AppCache.instance.user.username
                )
                AppDatabase.getInstance().bookDao().createNewBook(book)
                bookLiveData.postValue(book)
            }
        }, {})

        //netwook create
        return bookLiveData
    }

    fun getBookList(): LiveData<MutableList<Book>> {
        launchIO({
            bookListLiveData.postValue(AppDatabase.getInstance().bookDao().books()) }, {})
        return bookListLiveData
    }
}