package com.rh.heji.ui.user

import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.StringUtils
import org.json.JSONObject


object JWTParse {

    data class User(val name: String, val auth: List<String>, val token: String)

    private val localUser = User("localUser", mutableListOf(), "")

    fun getUser(jwt: String): User {
        if (jwt == "") return localUser
        val token = resolveToken(jwt)
        //val index = token.lastIndexOf(".")
        //var withoutSignature = token.substring(0, index)
        val header = token.split(".")[0]
        val payload = token.split(".")[1]
        var untrusted = String(EncodeUtils.base64Decode(payload))
        var jsonObject = JSONObject(untrusted)
        val username = jsonObject.opt("sub") as String
        val auth: String = jsonObject.opt("auth") as String
        val roles = auth.split(",")
        return User(username, roles, jwt)
    }

    private fun resolveToken(token: String): String {
        return if (!StringUtils.isEmpty(token) && token.startsWith("Bearer ")) {
            token.substring(7)
        } else token
    }
}