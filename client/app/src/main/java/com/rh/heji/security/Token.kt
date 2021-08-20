package com.rh.heji.security

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.FileUtils
import com.rh.heji.network.HejiNetwork
import com.tencent.mmkv.MMKV
import java.io.*
import java.nio.charset.StandardCharsets

object Token {
    private val mmkv: MMKV = MMKV.defaultMMKV()!!

    private const val TOKEN_FILE_NAME = "TOKEN"

    fun decodeToken(): String {
        var jwtTokenString = ""
        mmkv.decodeString(TOKEN_FILE_NAME, "")?.let {
            jwtTokenString = String(EncodeUtils.base64Decode(it))
        }
        return jwtTokenString
    }

    @Deprecated("使用mmkv实现", replaceWith = ReplaceWith("decodeToken()"))
    fun readTokenFile(context: Context): String {
        val tokenFile = File(context.filesDir, TOKEN_FILE_NAME)
        var token = ""
        if (tokenFile.exists()) {
            try {
                val inputStream = FileInputStream(tokenFile)
                val inputStreamReader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
                val stringBuilder = StringBuilder()
                try {
                    BufferedReader(inputStreamReader).use { reader ->
                        var line = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                } catch (e: IOException) {
                    // Error occurred when opening raw file for reading.
                    e.printStackTrace()
                } finally {
                    token = stringBuilder.toString()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        return String(EncodeUtils.base64Decode(token))
    }

    @Deprecated(
        "使用 encodeToken 由mmkv实现",
        replaceWith = ReplaceWith("encodeToken(jwtToken: String)")
    )
    fun writeTokenFile(context: Context, token: String?) {

        val file = File(context.filesDir.absolutePath)
        val tokenFile = File(file, TOKEN_FILE_NAME)
        FileUtils.createFileByDeleteOldFile(tokenFile)
        try {
            context.openFileOutput(TOKEN_FILE_NAME, Context.MODE_PRIVATE).use { fos ->
                fos.write(
                    EncodeUtils.base64Encode(token)
                )
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun encodeToken(jwtToken: String?) {
        mmkv.encode(TOKEN_FILE_NAME, EncodeUtils.base64Encode(jwtToken))
    }

    fun delete(context: Context) {
        mmkv.removeValueForKey(TOKEN_FILE_NAME)
    }

    private fun deleteTokenFile(context: Context) {
        val fileName = "TokenFile"
        val file = File(context.filesDir.absolutePath)
        val tokenFile = File(file, fileName)
        FileUtils.delete(tokenFile)
    }

    fun isLogin(): Boolean {
        return !TextUtils.isEmpty(decodeToken())
    }

    suspend fun authToken(token: String) {
        HejiNetwork.getInstance().hejiServer.auth(token)
    }

}