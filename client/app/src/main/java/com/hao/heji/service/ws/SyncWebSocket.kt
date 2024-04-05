package com.hao.heji.service.ws

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.config.Config
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import java.util.concurrent.TimeUnit

class SyncWebSocket() {
    enum class Status {
        OPEN, CLOSE, ERROR, RECONNECT
    }

    companion object {
        @Volatile
        private var instance: SyncWebSocket? = null
        var webSocket: WebSocket? = null
        private var status = Status.CLOSE

        //val URL ="ws://192.168.8.68:8888/ws/flow"
        var URL = "ws://${Config.serverUrl.split("://")[1]}"

        //val URL ="ws://192.168.8.68:8194/uchi-emcs"
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .pingInterval(58, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor())
            .build()
        var request: Request? = null
        fun getInstance(): SyncWebSocket {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = SyncWebSocket()
                    }
                }
            }
            return instance!!
        }
    }

    private lateinit var scope: CoroutineScope

    fun isRun(): Boolean {
        return status == Status.OPEN
    }

    fun send(text: String): Boolean {
        var success = false
        webSocket?.let {
            success = it.send(text)
        }
        return success
    }

    fun send(bytes: ByteString): Boolean {
        LogUtils.d("send msg ", bytes)
        if (!this::scope.isInitialized) {
            return false
        }
        if (webSocket == null) return false
        webSocket?.send(bytes = bytes)
        return true
    }

    fun close() {
        webSocket?.close(1000,"主动关闭")
        client.dispatcher.cancelAll()
        status = Status.CLOSE
    }

    private fun reconnect(token: String) {
        close()
        connect(URL, token, scope)
    }

    fun connect(
        wsUrl: String = URL,
        token: String, scope: CoroutineScope
    ) {
        if (status == Status.OPEN) {
            webSocket?.close(1000, "关闭旧的连接")
        }
        URL = wsUrl
        this.scope = scope
        val request = Request.Builder()
            .addHeader("Authorization", " Bearer $token")
            .url(wsUrl)
            .build()
        LogUtils.d("run: connect url=${wsUrl}")
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                LogUtils.d("onOpen:")
                status = Status.OPEN
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                LogUtils.d("onMessage: ${String(bytes.toByteArray())}")
                super.onMessage(webSocket, bytes)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                LogUtils.d("onMessage: $text")
                super.onMessage(webSocket, text)
                msgHandler(webSocket, text)
            }

            private fun msgHandler(webSocket: WebSocket, text: String) {
                when {
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                LogUtils.d("WebSocket onClosed: $code $reason")
                LogUtils.file("WebSocket onClosed: $code $reason")
                super.onClosed(webSocket, code, reason)
                //webSocket.close(code, null)
                status = Status.CLOSE
                LogUtils.d("WebSocket Status =$status")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                t.printStackTrace()
                LogUtils.file(t)
            }
        }
        webSocket = client.newWebSocket(request, webSocketListener)
//            client.dispatcher.executorService.shutdown()
    }

}