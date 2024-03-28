package com.hao.heji.ui.user.register

import java.io.Serializable

data class RegisterUser(
    var name: String,
    var password: String,
    var tel: String,
    var code: String
) : Serializable