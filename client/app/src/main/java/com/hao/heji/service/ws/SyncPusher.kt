package com.hao.heji.service.ws

import com.hao.heji.data.db.Bill
import com.hao.heji.moshi
import com.hao.heji.proto.Message

class SyncPusher(private val instance: SyncWebSocket) {
    fun addBill(bill: Bill) {
        val content = moshi.adapter(Bill::class.java).toJson(bill)
        val packet = createPacket(Message.Type.ADD_BILL, content)
        instance.send(packet.toBytes())
    }

    fun deleteBill(bid: String) {
        val packet = createPacket(Message.Type.DELETE_BILL, bid)
        instance.send(packet.toBytes())
    }

    fun updateBill(bill: Bill) {
        val content = moshi.adapter(Bill::class.java).toJson(bill)
        val packet = createPacket(Message.Type.UPDATE_BILL, content)
        instance.send(packet.toBytes())
    }
}