package com.hao.heji.sync

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.proto.Message
import com.hao.heji.sync.handler.AddBillHandler
import com.hao.heji.sync.handler.AddBookHandler
import com.hao.heji.sync.handler.DeleteBillHandler
import com.hao.heji.sync.handler.DeleteBookHandler
import com.hao.heji.sync.handler.IMessageHandler
import com.hao.heji.sync.handler.UpdateBillHandler
import com.hao.heji.sync.handler.UpdateBookHandler
import okhttp3.WebSocket
import okio.ByteString

class SyncReceiver {
    private val handlers = mutableListOf<IMessageHandler>()

    fun register() {

        register(AddBookHandler())
        register(DeleteBookHandler())
        register(UpdateBookHandler())

        register(AddBillHandler())
        register(DeleteBillHandler())
        register(UpdateBillHandler())

    }

    fun register(handler: IMessageHandler) {
        if (!handlers.contains(handler))
            handlers.add(handler)
    }

    fun unregister(handler: IMessageHandler) {
        if (handlers.contains(handler))
            handlers.remove(handler)
    }

    fun unregister() {
        handlers.clear()
    }

    fun onReceiver(webSocket: WebSocket, bytes: ByteString) {
        val packet = Message.Packet.parseFrom(bytes.toByteArray())
        for (i in handlers) {
            if (i.canHandle(packet)) {
                LogUtils.d("handle by ${i.javaClass.simpleName}",packet)
                i.handleMessage(webSocket, packet)
            }
        }
    }

}