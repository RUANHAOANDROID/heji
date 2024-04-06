package com.hao.heji.service.ws.handler

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.STATUS
import com.hao.heji.moshi
import com.hao.heji.proto.Message
import com.hao.heji.service.ws.createPacket
import com.hao.heji.service.ws.toBytes

class AddBillHandler : AbstractMessageHandler() {
    override fun canHandle(packet: Message.Packet): Boolean {
        return packet.type == Message.Type.ADD_BILL || isAck(packet)
    }

    override fun handle(packet: Message.Packet) {
        LogUtils.d(packet)
        val billDao = App.dataBase.billDao()
        if (isAck(packet)) {
            billDao.updateSyncStatus(billId = packet.content, STATUS.SYNCED)
            return
        }
        val bill = moshi.adapter(Bill::class.java).fromJson(packet.content)
        bill?.let {
            billDao.install(bill)
            if (syncWebSocket.isOpen()) {
                val packet = createPacket(
                    Message.Type.ADD_BILL_ACK,
                    bill.id,
                    mutableListOf(packet.fromId)
                )
                syncWebSocket.send(packet.toBytes())
            }
        }
    }

    private fun isAck(packet: Message.Packet) = packet.type == Message.Type.ADD_BILL_ACK
}

class DeleteBillHandler : AbstractMessageHandler() {
    override fun canHandle(packet: Message.Packet): Boolean {
        return packet.type == Message.Type.DELETE_BILL || isAck(packet)
    }

    private fun isAck(packet: Message.Packet) =
        packet.type == Message.Type.DELETE_BILL_ACK

    override fun handle(packet: Message.Packet) {
        LogUtils.d(packet)
        val billDao = App.dataBase.billDao()
        if (isAck(packet)) {
            billDao.deleteById(packet.content)
            return
        }
        billDao.deleteById(packet.content)
        val ackPacket = createPacket(Message.Type.DELETE_BILL_ACK, packet.content)
        syncWebSocket.send(ackPacket.toBytes())
    }
}

class UpdateBillHandler : AbstractMessageHandler() {
    override fun canHandle(packet: Message.Packet): Boolean {
        return packet.type == Message.Type.UPDATE_BILL || isAck(packet)
    }

    private fun isAck(packet: Message.Packet) =
        packet.type == Message.Type.UPDATE_BILL_ACK

    override fun handle(packet: Message.Packet) {
        LogUtils.d(packet)
        val billDao = App.dataBase.billDao()

        if (isAck(packet)) {
            billDao.updateSyncStatus(billId = packet.content, STATUS.SYNCED)
            return
        }
        val bill = moshi.adapter(Bill::class.java).fromJson(packet.content)
        bill?.let {
            billDao.update(bill)
            if (syncWebSocket.isOpen()) {
                val packet = createPacket(
                    Message.Type.UPDATE_BILL_ACK,
                    bill.id,
                    mutableListOf(packet.fromId)
                )
                syncWebSocket.send(packet.toBytes())
            }
        }
    }
}