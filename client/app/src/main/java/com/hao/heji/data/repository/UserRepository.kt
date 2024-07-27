package com.hao.heji.data.repository

import com.hao.heji.network.HttpManager
import com.hao.heji.ui.user.register.RegisterUser
import retrofit2.await

class UserRepository {
    suspend fun register(registerUser: RegisterUser) =
        HttpManager.getInstance().register(registerUser)

    suspend fun login(username: String, password: String) =
        HttpManager.getInstance().login(username, password)
}