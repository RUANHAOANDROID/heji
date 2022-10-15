package com.rh.heji.ui.user.login

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.ui.user.security.UserToken
import com.rh.heji.utlis.launchIO

internal class LoginViewModel : BaseViewModel<LoginAction, LoginUiState>() {


    override fun doAction(action: LoginAction) {
        super.doAction(action)
        when (action) {
            is LoginAction.Login -> {
                login(action.userName, action.password)
            }
        }
    }

    private fun login(username: String, password: String) {
        launchIO({
            var requestBody = HejiNetwork.getInstance().login(
                username,
                encodePassword(password)
            )
            var token = requestBody.data
            UserToken.saveToken(token)
            App.setUser(currentUser = JWTParse.getUser(token))
            ToastUtils.showLong(requestBody.data)
            initDataBase(App.user)
            initBook(App.user)
            send(LoginUiState.Success(token))
        }, {
            send(LoginUiState.Error(it))
        })

    }


    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }

    private fun auth(token: UserToken) {
        //在服务验证一次拿用户，登陆仅仅返回Token
        //var user =HejiNetwork.getInstance().auth(token.trim().split("Bearer")[1]).apply {}
    }

    private fun initDataBase(user: JWTParse.User) {
        App.setDataBase(user.name)
    }

    private suspend fun initBook(user: JWTParse.User) {
        //账本同步到本地，如果没有账本，创建一个默认账本
        val response = HejiNetwork.getInstance().bookPull()
        if (response.code == 0 && response.data.isNotEmpty()) {
            response.data.forEach {
                App.dataBase.bookDao().upsert(it)
                if (it.firstBook == 0) {
                    App.setCurrentBook(it)
                }
            }

        } else {
            createDefBook()
        }
    }

    /**
     * 在线创建失败则创建默认账本
     *
     */
    @Deprecated("server created book")
    private fun createDefBook() {
        val bookDao = App.dataBase.bookDao()
        if (bookDao.count() == 0) {
            val firstBook =
                Book(
                    id = ObjectId().toHexString(),
                    name = "个人账本",
                    firstBook = 0,
                    createUser = App.user.name,
                    type = "个人账本"
                ).apply {
                    synced = STATUS.NOT_SYNCED
                }
            App.setCurrentBook(firstBook)
            bookDao.insert(firstBook)
        }
    }
}






