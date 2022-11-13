package com.rh.heji.ui.user.register

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.Config
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.network.HttpManager
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch

internal class RegisterViewModel : BaseViewModel<RegisterAction, RegisterUiState>() {

    override fun doAction(action: RegisterAction) {
        super.doAction(action)
        when (action) {
            is RegisterAction.Register -> {
                register(action.username, action.tel, action.code, action.password)
            }
        }
    }

    private fun register(
        username: String,
        tel: String,
        code: String,
        password: String
    ) {
        var user = RegisterUser(
            name = username,
            tel = tel,
            password = encodePassword(password),
            code = code
        )

        launch({
            var response = HttpManager.getInstance().register(user)
            val body = response.data.apply {
                this.password = password//本地输入的未加密的密码
            }
            send(RegisterUiState.Success(body))
        }, {
            ToastUtils.showLong(it.message)
        })

    }
    private suspend fun remoteCreateFirstBook(): Book {
        val firstBook = Book(
            id = ObjectId().toHexString(),
            name = "个人账本",
            createUser = Config.user.name,
            firstBook = 0,
            type = "个人账本",
        )
        val response = HttpManager.getInstance().bookCreate(firstBook)
        if (response.success()) {
            App.dataBase.bookDao().insert(firstBook)
        }
        return firstBook
    }
    private fun encodePassword(password: String): String {
        return EncryptUtils.encryptSHA512ToString(String(EncryptUtils.encryptSHA512(password.toByteArray())))
    }
}

