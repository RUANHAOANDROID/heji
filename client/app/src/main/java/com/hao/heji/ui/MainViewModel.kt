package com.hao.heji.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.data.db.Book
import com.hao.heji.data.repository.BookRepository
import com.hao.heji.ui.user.JWTParse
import com.hao.heji.ui.user.login.LoginViewModel
import com.hao.heji.utils.YearMonth
import kotlinx.coroutines.launch
import java.util.*

/**
 * @date: 2020/11/3
 * @author: 锅得铁
 * # APP运行时 UI常量共享存储
 */
class MainViewModel : ViewModel() {
    private val bookRepository = BookRepository()

    init {
        LogUtils.d(
            "MainViewModel",
            "Config enableOfflineMode=${Config.enableOfflineMode}",
            "Config isInitBook=${Config.book}",
            "Config isInitUser=${Config.user}"
        )
    }

    /**
     * 全局选择的年月（home to subpage）
     */
    var globalYearMonth: YearMonth =
        YearMonth(Calendar.getInstance()[Calendar.YEAR], Calendar.getInstance()[Calendar.MONTH] + 1)

    /**
     * 当前年月
     */
    val currentYearMonth by lazy {
        YearMonth(
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH] + 1
        )
    }

    fun switchBook() {
        viewModelScope.launch {
            if (Config.enableOfflineMode) {

                val bookDao = App.dataBase.bookDao()
                if (bookDao.count() == 0) {
                    bookDao.insert(Config.book)
                } else {
                    val books = bookDao.findInitBook(Config.user.id)
                    books.firstOrNull()?.let {
                        Config.setBook(it)
                    }
                }
            } else {
                createBook(Config.user)
            }
        }
    }

    private suspend fun createBook(newUser: JWTParse.User) {
        val remoteBooks = bookRepository.bookList().data
        val bookDao = App.dataBase.bookDao()
        var initialBook = Book(name = "个人账本", crtUserId = newUser.id, isInitial = true)
        if (remoteBooks.isNotEmpty()) {
            remoteBooks.forEach {
                bookDao.upsert(it)
                if (it.isInitial) {
                    initialBook = it//当服务器存在初始账本
                }
            }
        } else {
            val books = bookDao.findBookIdsByUser(newUser.id)//查询本地是否存在账本
            if (books.size <= 0) {
                bookRepository.createBook(initialBook)
            }
        }
        Config.setBook(initialBook)
        Config.save(newUser, initialBook, offLine = false)
    }


}