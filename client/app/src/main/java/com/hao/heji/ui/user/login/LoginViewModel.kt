package com.hao.heji.ui.user.login

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.EncryptUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.config.InitBook
import com.hao.heji.config.LocalUser
import com.hao.heji.config.store.DataStoreManager
import com.hao.heji.data.db.Book
import com.hao.heji.data.db.STATUS
import com.hao.heji.data.repository.BookRepository
import com.hao.heji.network.HttpManager
import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.ui.user.JWTParse
import com.hao.heji.data.repository.UserRepository
import com.hao.heji.utils.launch
import com.hao.heji.utils.launchIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class LoginViewModel : BaseViewModel<LoginAction, LoginUiState>() {
    private val userRepository = UserRepository()
    private val bookRepository = BookRepository()
    override fun doAction(action: LoginAction) {
        when (action) {
            is LoginAction.Login -> {
                login(action.tel, action.password)
            }

            is LoginAction.EnableOfflineMode -> {
                enableOfflineMode()
            }

            is LoginAction.SaveServerUrl -> {
                viewModelScope.launch {
                    Config.setServerUrl(action.address)
                    HttpManager.getInstance().redirectServer()
                }

            }

            LoginAction.GetServerUrl -> {
                launchIO({
                    DataStoreManager.getServerUrl().collect {
                        send(LoginUiState.ShowServerSetting(it))
                    }
                })
            }
        }
    }

    private fun login(tel: String, password: String) {
        launch({
            var resp = userRepository.login(
                tel,
                encodePassword(password)
            )
            val newUser = JWTParse.getUser(resp.data)
            Config.setUser(newUser)
            Config.enableOfflineMode(false)
            App.switchDataBase(newUser.id)
            val remoteBooks = getRemoteBooks()
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
                    bookDao.insert(initialBook)
                    val response = bookRepository.createBook(initialBook)
                    if (response.success()) {
                        initialBook.syncStatus = STATUS.SYNCED
                        bookDao.upsert(initialBook)
                    }
                }
            }
            Config.setBook(initialBook)
            Config.save(newUser, initialBook, offLine = false)
            send(LoginUiState.LoginSuccess(resp.data))

        }, {
            send(LoginUiState.LoginError(it))
        })

    }


    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }

    private suspend fun getRemoteBooks(): MutableList<Book> {
        runCatching<MutableList<Book>> {
            bookRepository.bookList().data
        }.onSuccess {
            return it
        }.onFailure {
            return mutableListOf()
        }

        return mutableListOf()
    }

    /**
     * 开启离线使用模式
     */
    private fun enableOfflineMode() {
        launchIO({
            Config.save(LocalUser, InitBook, true)
            val bookDao = App.dataBase.bookDao()
            if (bookDao.count() == 0) {
                bookDao.insert(Config.book)
            } else {
                val books = bookDao.findInitBook(Config.user.id)
                books.firstOrNull()?.let {
                    Config.setBook(it)
                }
            }
            send(LoginUiState.OfflineRun)
        })

    }
}






