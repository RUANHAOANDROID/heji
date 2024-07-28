package com.hao.heji.sync.handler

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.STATUS
import com.hao.heji.moshi
import com.hao.heji.proto.Message
import com.hao.heji.sync.createPacket
import com.hao.heji.sync.toBytes
import okhttp3.WebSocket

class AddBillHandler : IMessageHandler {

    override fun canHandle(packet: Message.Packet): Boolean {
        val add = packet.type == Message.Type.ADD_BILL
        val ack = packet.type == Message.Type.ADD_BILL_ACK
        return add || ack
    }

    override fun handleMessage(webSocket: WebSocket, packet: Message.Packet) {
        LogUtils.d("开始处理消息 type=${packet.type}")
        val billDao = App.dataBase.billDao()
        if (packet.type == Message.Type.ADD_BILL_ACK) {
            billDao.updateSyncStatus(billId = packet.content, STATUS.SYNCED)
            return
        }
        val bill = moshi.adapter(Bill::class.java).fromJson(packet.content)
        bill?.let {
            billDao.install(bill)
            val packet = createPacket(
                Message.Type.ADD_BILL_ACK,
                bill.id,
                mutableListOf(packet.fromId)
            )
            webSocket.send(packet.toBytes())
        }
    }
}

class DeleteBillHandler : IMessageHandler {
    override fun canHandle(packet: Message.Packet): Boolean {
        return packet.type == Message.Type.DELETE_BILL || isAck(packet)
    }

    override fun handleMessage(webSocket: WebSocket, packet: Message.Packet) {
        LogUtils.d(packet)
        val billDao = App.dataBase.billDao()
        if (isAck(packet)) {
            val id =packet.content
            billDao.deleteById(id)
            return
        }
        billDao.deleteById(packet.content)
        val ackPacket = createPacket(Message.Type.DELETE_BILL_ACK, packet.content)
        webSocket.send(ackPacket.toBytes())
    }

    private fun isAck(packet: Message.Packet) =
        packet.type == Message.Type.DELETE_BILL_ACK
}

class UpdateBillHandler : IMessageHandler {
    override fun canHandle(packet: Message.Packet): Boolean {
        return packet.type == Message.Type.UPDATE_BILL || isAck(packet)
    }

    override fun handleMessage(webSocket: WebSocket, packet: Message.Packet) {
        LogUtils.d(packet)
        val billDao = App.dataBase.billDao()

        if (isAck(packet)) {
            billDao.updateSyncStatus(billId = packet.content, STATUS.SYNCED)
            return
        }
        val bill = moshi.adapter(Bill::class.java).fromJson(packet.content)
        bill?.let {
            billDao.update(bill)
            val packet = createPacket(
                Message.Type.UPDATE_BILL_ACK,
                bill.id,
                mutableListOf(packet.fromId)
            )
            webSocket.send(packet.toBytes())

        }
    }

    private fun isAck(packet: Message.Packet) =
        packet.type == Message.Type.UPDATE_BILL_ACK

}