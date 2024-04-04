package com.hao.heji.service.ws

import com.hao.heji.proto.Message
import okio.ByteString

class MessagePusher {
    fun ack(id: String) {
        val ackMsg = Message.Packet.newBuilder()
            .setId(id)
            .setType(Message.Type.ACK)
            .build()
        SyncWebSocket.getInstance().send(ackMsg.toBytes())
    }

    private inline fun Message.Packet.toBytes(): ByteString {
        return ByteString.of(*this.toByteArray())
    }
}