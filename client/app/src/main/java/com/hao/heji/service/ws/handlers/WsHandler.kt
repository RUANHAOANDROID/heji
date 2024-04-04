package com.hao.heji.service.ws.handlers

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.proto.Message
import kotlinx.coroutines.flow.catch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

class WsHandler(val context: Context) {


    companion object {
        const val TAG = "WSHandler"
    }

    suspend fun cmdHandler(wsMsg: Message) {

    }

    private fun uploadFile(
        serverUrl: String,
        filePath: String,
        call: (code: Int, result: String) -> Unit
    ) {
        val client = OkHttpClient.Builder()
            .readTimeout(30000L, TimeUnit.SECONDS)
            .writeTimeout(30000L, TimeUnit.SECONDS)
            .build()
        val file = File(filePath)
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .build()
        val request: Request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                call(1, response.message)
                return
            }
            val body = response.body?.string()
            LogUtils.d("File uploaded successfully$body")
            call(0, body!!)
        }
    }
}