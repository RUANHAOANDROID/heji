package com.hao.heji.service.ws.handler

import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.proto.Message
import com.hao.heji.service.ws.SyncWebSocket


abstract class AbstractMessageHandler : IMessageHandler {
    private var nextHandler: IMessageHandler? = null
    protected val syncWebSocket = SyncWebSocket.getInstance()
    override fun setNextHandler(handler: IMessageHandler): IMessageHandler {
        this.nextHandler = handler
        return handler
    }

    override fun handleMessage(packet: Message.Packet) {
        if (canHandle(packet)) {
            handle(packet)
            return
        }
        nextHandler?.handleMessage(packet)
    }

    protected abstract fun canHandle(packet: Message.Packet): Boolean
    protected abstract fun handle(packet: Message.Packet)


}