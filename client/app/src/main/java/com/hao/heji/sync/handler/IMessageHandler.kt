package com.hao.heji.sync.handler

import com.hao.heji.proto.Message
import okhttp3.WebSocket

interface IMessageHandler {
    fun canHandle(packet: Message.Packet): Boolean
    fun handleMessage(webSocket: WebSocket, packet: Message.Packet)
}