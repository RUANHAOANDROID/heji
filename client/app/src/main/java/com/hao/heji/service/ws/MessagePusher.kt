package com.hao.heji.service.ws

import com.google.protobuf.Any
import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.proto.Message
import okio.ByteString


class MessagePusher {
    private val connect = SyncWebSocket.getInstance()
    fun ack(id: String) {
        val ackMsg = Message.Packet.newBuilder()
            .setId(id)
            .setType(Message.Type.ACK)
            .build()
        connect.send(ackMsg.toBytes())
    }

    fun addBill(bill: Bill) {
        val bill: Message.Bill = Message.Bill.newBuilder()
            .setId("123")
            .setBookId("456")
            .setMoney("100")
            .setCategory("test")
            .setCreateUser("user")
            .setCreateTime("2024-04-06")
            .setUpdateTime("2024-04-06")
            .setRemark("remark")
            .addImages("image1")
            .addImages("image2")
            .build()
        // 将 Bill 消息序列化为字节数组
        val billBytes: ByteArray = bill.toByteArray()

        // 创建一个 Any 消息
        val anyBill = Any.pack<com.google.protobuf.Message>(bill)

        val pack = Message.Packet.newBuilder()
            .setId(ObjectId.get().toHexString())
            .addToId("hao88")
            .setFromId("ahao")
            .setType(Message.Type.ADD_BILL)
            .addContent(anyBill)
            .build()
        connect.send(pack.toBytes())
    }

    private inline fun Message.Packet.toBytes(): ByteString {
        return ByteString.of(*this.toByteArray())
    }
}