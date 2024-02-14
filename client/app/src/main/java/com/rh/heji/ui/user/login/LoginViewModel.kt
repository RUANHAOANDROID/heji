package com.rh.heji.ui.user.login

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.config.Config
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.network.HttpManager
import com.rh.heji.config.store.DataStoreManager
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utils.launch
import com.rh.heji.utils.launchIO

internal class LoginViewModel : BaseViewModel<LoginAction, LoginUiState>() {


    override fun doAction(action: LoginAction) {
        super.doAction(action)
        when (action) {
            is LoginAction.Login -> {
                login(action.tel, action.password)
            }
            is LoginAction.EnableOfflineMode -> {
                enableOfflineMode()
            }
        }
    }

    private fun login(tel: String, password: String) {
        launchIO({
            var requestBody = HttpManager.getInstance().login(
                tel,
                encodePassword(password)
            )
            ToastUtils.showLong(requestBody.data)
            var token = requestBody.data
            Config.setToken(token)
            val user = JWTParse.getUser(token)
            Config.setUser(user)
            Config.setUseMode(enableOffline = false)
            App.switchDataBase(Config.user.name)
            val initBooksJob = launch({
                val remoteBooks = getRemoteBooks()
                val bookDao = App.dataBase.bookDao()
                if (remoteBooks.isNotEmpty()) {
                    remoteBooks.forEach {
                        bookDao.upsert(it)
                        if (it.firstBook) {
                            Config.setBook(it)
                        }
                    }
                } else {
                    val books = bookDao.findBookIdsByUser(user.name)
                    if (books.size <= 0) {
                        val firstBook = Book(name = "个人账本", createUser = user.name, firstBook = true)
                        bookDao.insert(firstBook)
                        val response = HttpManager.getInstance().bookCreate(firstBook)
                        if (response.success()) {
                            firstBook.syncStatus =STATUS.SYNCED
                            bookDao.upsert(firstBook)
                        }
                    }

                }
            })
            initBooksJob.join()
            DataStoreManager.saveUseMode(enableOffline = false)
            DataStoreManager.saveBook(Config.book)
            send(LoginUiState.LoginSuccess(token))
        }, {
            send(LoginUiState.LoginError(it))
        })

    }


    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }

    private suspend fun getRemoteBooks(): MutableList<Book> {
        val response = HttpManager.getInstance().bookPull()
        if (response.success()) {
            return response.data
        }
        return mutableListOf()
    }

    /**
     * 开启离线使用模式
     */
    private fun enableOfflineMode() {
        launchIO({
            with(Config) {
                App.switchDataBase(localUser.name)
                setBook(defaultBook)
                setUser(localUser)
                setUseMode(true)
            }
            val bookDao = App.dataBase.bookDao()
            if (bookDao.count() == 0) {
                bookDao.insert(Config.defaultBook)
            }
            DataStoreManager.saveUseMode(true)
            DataStoreManager.saveBook(Config.defaultBook)
            send(LoginUiState.OfflineRun)
        })

    }
}






