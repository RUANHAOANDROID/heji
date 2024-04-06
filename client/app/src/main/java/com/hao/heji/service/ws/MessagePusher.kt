package com.hao.heji.service.ws

import com.blankj.utilcode.util.GsonUtils
import com.google.protobuf.Any
import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.moshi
import com.hao.heji.proto.Message
import okio.ByteString


class MessagePusher {
    private val connect = SyncWebSocket.getInstance()
    fun ack(id: String, ok: Boolean = false) {
        val type = if (ok) Message.Type.ACK_OK else Message.Type.ACK_ERR
        val ackMsg = Message.Packet.newBuilder()
            .setId(id)
            .setType(type)
            .build()
        connect.send(ackMsg.toBytes())
    }

    fun addBill(bill: Bill) {
        val pack = Message.Packet.newBuilder()
            .setId(ObjectId.get().toHexString())
            .addToId("hao88")
            .setFromId("ahao")
            .setType(Message.Type.ADD_BILL)
            .setContent(moshi.adapter(Bill::class.java).toJson(bill))
            .build()
        connect.send(pack.toBytes())
    }

    private inline fun Message.Packet.toBytes(): ByteString {
        return ByteString.of(*this.toByteArray())
    }
}