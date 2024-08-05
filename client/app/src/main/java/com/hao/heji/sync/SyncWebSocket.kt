package com.hao.heji.sync

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.proto.Message
import com.hao.heji.sync.handler.AddBillHandler
import com.hao.heji.sync.handler.DeleteBillHandler
import com.hao.heji.sync.handler.MessageHandler
import com.hao.heji.sync.handler.UpdateBillHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import java.util.concurrent.TimeUnit

class SyncWebSocket {
    private var wsUrl = ""
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor())
        .build()

    enum class Status {
        OPEN, CLOSE, ERROR, CONNECTING
    }

    private var status = Status.CLOSE
    private var webSocket: WebSocket? = null
    private var request: Request? = null


    private var handlers: MessageHandler = MessageHandler()

    companion object {
        @Volatile
        private var instance: SyncWebSocket? = null
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

    fun send(packet: Message.Packet): Boolean {
        LogUtils.d(packet.toString())
        if (!this::scope.isInitialized) {
            return false
        }
        if (status != Status.OPEN) {
            LogUtils.d("webSocket 未链接")
            return false
        }
        if (webSocket == null)
            return false
        webSocket?.send(bytes = packet.toBytes())
        return true
    }

    fun close() {
        LogUtils.d("close websocket")
        webSocket?.close(1000, "主动关闭")
        client.dispatcher.cancelAll()
        status = Status.CLOSE
        client.dispatcher.executorService.shutdown()
    }

    fun isOpen(): Boolean {
        return status == Status.OPEN
    }

    fun isReconnect(): Boolean {
        return status == Status.CONNECTING
    }

    fun isClose(): Boolean {
        return status == Status.CLOSE
    }

    fun isError(): Boolean {
        return status == Status.ERROR
    }

    private fun reconnect(token: String) {
        status = Status.CONNECTING
        close()
        connect(wsUrl, token, scope)
    }

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            LogUtils.d("WebSocket onOpen:${response}")
            status = Status.OPEN
            handlers.register(AddBillHandler())
            handlers.register(DeleteBillHandler())
            handlers.register(UpdateBillHandler())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            LogUtils.d("onMessage",webSocket,text)
        }
        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            scope.launch(Dispatchers.IO) {
                handlers.handler(webSocket, bytes)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            status = Status.CLOSE
            super.onClosed(webSocket, code, reason)
            LogUtils.d("WebSocket onClosed: $code $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            status = Status.ERROR
            super.onFailure(webSocket, t, response)
            LogUtils.e(webSocket,t,response)
        }
    }

    fun connect(
        wsUrl: String,
        token: String, scope: CoroutineScope
    ) {
        LogUtils.d(wsUrl, token)
        if (status == Status.OPEN) {
            webSocket?.close(1000, "关闭旧的连接")
        }
        this.wsUrl = wsUrl
        this.scope = scope
        request = Request.Builder()
            .addHeader("Authorization", " Bearer $token")
            .url(wsUrl)
            .build()
        LogUtils.d("run: connect url=${wsUrl}")
        request?.let {
            webSocket = client.newWebSocket(it, webSocketListener)
        }
    }
}