package com.rh.heji.ui.user.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.currentBook
import com.rh.heji.currentUser
import com.rh.heji.data.AppDatabase
import com.rh.heji.expenditureDefaultCategory
import com.rh.heji.incomeDefaultCategory
import com.rh.heji.network.HejiNetwork
import com.rh.heji.security.Token
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO

class LoginViewModel : BaseViewModel() {
    private val loginLiveData: MediatorLiveData<String> = MediatorLiveData()
    fun login(username: String, password: String): LiveData<String> {
        launchIO({
            var requestBody = HejiNetwork.getInstance().login(username, password)
            var token = requestBody.data
            Token.encodeToken(token)
            currentUser = JWTParse.getUser(token)
            ToastUtils.showLong(requestBody.data)
            loginLiveData.postValue(token)
        }, {
            ToastUtils.showLong("登陆错误:${it.message}")
        })
        launch({}, {})
        return loginLiveData
    }

    suspend fun initBook() {
        val response = HejiNetwork.getInstance().bookPull()
        if (response.code == 0 && response.data.isNotEmpty()) {
            response.data.forEach {
                AppDatabase.getInstance().bookDao().upsert(it)
            }
        }else{
            createDefBook()
        }
    }

    private fun createDefBook() {
        AppDatabase.getInstance().let {
            val bookDao = it.bookDao()
            val categoryDao = it.categoryDao()
            if (bookDao.count() == 0) {
                bookDao.insert(currentBook)
            }
            if (categoryDao.count() == 0) {
                categoryDao.insert(incomeDefaultCategory)
                categoryDao.insert(expenditureDefaultCategory)
            }
        }
    }
}






