package com.hao.heji.sync

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.proto.Message
import okio.ByteString

inline fun createPacket(
    type: Message.Type,
    content: String,
    id: String= ObjectId.get().toHexString(),
    toUsers: MutableList<String>? = null
): Message.Packet {
    val newBuilder = Message.Packet.newBuilder()
        .setId(id)
        .setSenderId(com.hao.heji.config.Config.user.id)
        .setType(type)
        .setContent(content)
    toUsers?.let {
        if (it.isNotEmpty()) {
            newBuilder.addAllReceiverIds(it)
        }
    }
    return newBuilder.build()
}

inline fun Message.Packet.toBytes(): ByteString {
    LogUtils.d(this.toString())
    return ByteString.of(*this.toByteArray())
}

inline fun Message.Packet.convertToAck(
    type: Message.Type,
    content: String,
): Message.Packet {
    return createPacket(type, content,id, mutableListOf(senderId))
}