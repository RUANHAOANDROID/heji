package com.rh.heji.security

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.FileUtils
import com.rh.heji.TOKEN
import java.io.*
import java.nio.charset.StandardCharsets

class Token(val context: Context) {
    // Error occurred when opening raw file for reading.
    val tokenString: String= TOKEN
//        get() {
//            val tokenFile = File(context.filesDir, "TokenFile")
//            var token = ""
//            if (tokenFile.exists()) {
//                try {
//                    val inputStream = FileInputStream(tokenFile)
//                    val inputStreamReader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
//                    val stringBuilder = StringBuilder()
//                    try {
//                        BufferedReader(inputStreamReader).use { reader ->
//                            var line = reader.readLine()
//                            while (line != null) {
//                                stringBuilder.append(line)
//                                line = reader.readLine()
//                            }
//                        }
//                    } catch (e: IOException) {
//                        // Error occurred when opening raw file for reading.
//                        e.printStackTrace()
//                    } finally {
//                        token = stringBuilder.toString()
//                    }
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                }
//            }
//            return String(EncodeUtils.base64Decode(token))
//        }

    fun save(token: String?) {
        val fileName = "TokenFile"
        val file = File(context.filesDir.absolutePath)
        val tokenFile = File(file, fileName)
        FileUtils.createFileByDeleteOldFile(tokenFile)
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
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

    fun delete() {
        val fileName = "TokenFile"
        val file = File(context.filesDir.absolutePath)
        val tokenFile = File(file, fileName)
        FileUtils.delete(tokenFile)
    }

    val isLogin: Boolean
        get() = !TextUtils.isEmpty(tokenString)

    override fun toString(): String {
        return tokenString
    }
}