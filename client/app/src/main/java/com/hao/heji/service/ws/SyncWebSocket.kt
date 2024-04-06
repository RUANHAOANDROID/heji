package com.hao.heji.service.ws

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.proto.Message
import com.hao.heji.service.ws.handler.AddBillHandler
import com.hao.heji.service.ws.handler.DeleteBillHandler
import com.hao.heji.service.ws.handler.IMessageHandler
import com.hao.heji.service.ws.handler.UpdateBillHandler
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

class SyncWebSocket() {
    private var wsUrl = ""
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(58, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor())
        .build()

    enum class Status {
        OPEN, CLOSE, ERROR, RECONNECT
    }

    private var status = Status.CLOSE
    private var webSocket: WebSocket? = null
    private var request: Request? = null

    private val messageHandler: IMessageHandler by lazy {
        AddBillHandler().setNextHandler(DeleteBillHandler()).setNextHandler(UpdateBillHandler())
    }


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
        webSocket?.close(1000, "主动关闭")
        client.dispatcher.cancelAll()
        status = Status.CLOSE
    }

    private fun reconnect(token: String) {
        close()
        connect(wsUrl, token, scope)
    }

    fun isOpen(): Boolean {
        return status == Status.OPEN
    }

    fun connect(
        wsUrl: String,
        token: String, scope: CoroutineScope
    ) {
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
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                LogUtils.d("onOpen:")
                status = Status.OPEN
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                LogUtils.d("onMessage: ${String(bytes.toByteArray())}")
                super.onMessage(webSocket, bytes)
                processorMessage(webSocket, bytes)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                LogUtils.d("onMessage: $text")
                super.onMessage(webSocket, text)

            }

            private fun processorMessage(webSocket: WebSocket, bytes: ByteString) {
                LogUtils.d("processorMessage")
                val packet = Message.Packet.parseFrom(bytes.toByteArray())
                scope.launch(Dispatchers.IO){
                    messageHandler.handleMessage(packet)
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
        request?.let {
            webSocket = client.newWebSocket(it, webSocketListener)
        }
//            client.dispatcher.executorService.shutdown()
    }

}