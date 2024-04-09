package com.hao.heji.data.repository

import com.hao.heji.network.HttpManager
import com.hao.heji.ui.user.register.RegisterUser
import retrofit2.await

class UserRepository {
    private val server = HttpManager.getInstance().server()
    suspend fun register(registerUser: RegisterUser) = server.register(registerUser).await()
    suspend fun login(username: String, password: String) =
        server.login(mapOf("tel" to username, "password" to password)).await()
}