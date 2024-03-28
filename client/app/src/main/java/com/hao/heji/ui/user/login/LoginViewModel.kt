package com.hao.heji.ui.user.login

import com.blankj.utilcode.util.EncryptUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.config.InitBook
import com.hao.heji.config.LocalUser
import com.hao.heji.data.db.Book
import com.hao.heji.data.db.STATUS
import com.hao.heji.network.HttpManager
import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.ui.user.JWTParse
import com.hao.heji.utils.launch
import com.hao.heji.utils.launchIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        launch({
            var resp = HttpManager.getInstance().login(
                tel,
                encodePassword(password)
            )
            val newUser = JWTParse.getUser(resp.data)
            App.switchDataBase(newUser.id)
            with(Config) {
                user = newUser
                enableOfflineMode = false
            }
            withContext(Dispatchers.IO) {
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
                        val response = HttpManager.getInstance().createBook(initialBook)
                        if (response.success()) {
                            initialBook.syncStatus = STATUS.SYNCED
                            bookDao.upsert(initialBook)
                        }
                    }
                }
                Config.book = initialBook
                Config.save()
                send(LoginUiState.LoginSuccess(resp.data))
            }
        }, {
            send(LoginUiState.LoginError(it))
        })

    }


    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }

    private suspend fun getRemoteBooks(): MutableList<Book> {
        runCatching<MutableList<Book>> {
            HttpManager.getInstance().bookList().data
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
            with(Config) {
                App.switchDataBase(LocalUser.id)
                book = InitBook
                user = LocalUser
                enableOfflineMode = true
                save()
            }
            val bookDao = App.dataBase.bookDao()
            if (bookDao.count() == 0) {
                bookDao.insert(Config.book)
            }
            send(LoginUiState.OfflineRun)
        })

    }
}






