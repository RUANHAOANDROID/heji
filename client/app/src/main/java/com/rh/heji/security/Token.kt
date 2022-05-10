package com.rh.heji.security

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.App
import com.rh.heji.DataStoreManager
import com.rh.heji.PreferencesKey
import com.rh.heji.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.*
import java.nio.charset.StandardCharsets

object Token {


    private const val TOKEN_FILE_NAME = "TOKEN"

    suspend fun getToken(): Flow<String?> {
        return DataStoreManager.getToken()
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

    suspend fun saveToken(jwtToken: String) {
        DataStoreManager.saveToken(jwtToken)
    }

    suspend fun deleteToken() {
        return DataStoreManager.deleteToken()
    }

    private fun deleteTokenFile(context: Context) {
        val fileName = "TokenFile"
        val file = File(context.filesDir.absolutePath)
        val tokenFile = File(file, fileName)
        FileUtils.delete(tokenFile)
    }
}