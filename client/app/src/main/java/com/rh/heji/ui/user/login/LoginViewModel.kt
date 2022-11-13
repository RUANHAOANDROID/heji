package com.rh.heji.ui.user.login

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.Config
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.network.HttpManager
import com.rh.heji.store.DataStoreManager
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO

internal class LoginViewModel : BaseViewModel<LoginAction, LoginUiState>() {


    override fun doAction(action: LoginAction) {
        super.doAction(action)
        when (action) {
            is LoginAction.Login -> {
                login(action.userName, action.password)
            }
            is LoginAction.EnableOfflineMode -> {
                enableOfflineMode()
            }
        }
    }

    private fun login(username: String, password: String) {
        launchIO({
            val loginJob = launch({

            })
            var requestBody = HttpManager.getInstance().login(
                username,
                encodePassword(password)
            )
            ToastUtils.showLong(requestBody.data)

            var token = requestBody.data

            Config.setToken(token)
            Config.setUser(JWTParse.getUser(token))
            Config.setUseMode(enableOffline = false)
            App.switchDataBase(Config.user.name)
            val initBooksJob = launch({
                val remoteBooks = getRemoteBooks()
                if (remoteBooks.isNotEmpty()) {
                    remoteBooks.forEach {
                        App.dataBase.bookDao().upsert(it)
                        if (it.firstBook == 0) {
                            Config.setBook(it)
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

    private suspend fun getRemoteBook(user: JWTParse.User) {
        //账本同步到本地，如果没有账本，创建一个默认账本
        val response = HttpManager.getInstance().bookPull()
        if (response.code == 0 && response.data.isNotEmpty()) {
            response.data.forEach {
                App.dataBase.bookDao().upsert(it)
                if (it.firstBook == 0) {
                    Config.setBook(it)
                }
            }
        } else {
            val firstBook = Book(
                id = "0",
                name = "个人账本",
                createUser = user.name,
                firstBook = 0,
                type = "离线账本",
            )
            createBook(firstBook)
            Config.setBook(Config.defaultBook)
        }
    }

    /**
     * 在线创建失败则创建默认账本
     *
     */

    private fun createBook(book: Book = Config.defaultBook) {
        val bookDao = App.dataBase.bookDao()
        if (bookDao.count() == 0) {
            bookDao.insert(book)
        }
    }

    /**
     * 开启离线使用模式
     */
    private fun enableOfflineMode() {
        with(Config) {
            App.switchDataBase(localUser.name)
            setBook(defaultBook)
            setUser(localUser)
            setUseMode(true)
        }
        launchIO({
            createBook(Config.defaultBook)
            DataStoreManager.saveUseMode(true)
            DataStoreManager.saveBook(Config.defaultBook)
            send(LoginUiState.OfflineRun())
        })

    }
}






