package com.hao.heji.sync.handler

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.proto.Message
import okhttp3.WebSocket
import okio.ByteString

class MessageHandler {

    private val handlers = mutableListOf<IMessageHandler>()

    fun register(handler: IMessageHandler) {
        if (!handlers.contains(handler))
            handlers.add(handler)
    }

    fun unregister(handler: IMessageHandler) {
        if (handlers.contains(handler))
            handlers.remove(handler)
    }

    fun clear() {
        handlers.clear()
    }

    fun handler(webSocket: WebSocket, bytes: ByteString) {
        val packet = Message.Packet.parseFrom(bytes.toByteArray())
        for (i in handlers) {
            if (i.canHandle(packet)) {
                LogUtils.d("Type=${packet.type}", "handle by ${i.javaClass.simpleName}")
                i.handleMessage(webSocket, packet)
            }
        }
    }
}