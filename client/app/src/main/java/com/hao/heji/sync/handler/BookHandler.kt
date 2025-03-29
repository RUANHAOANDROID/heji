package com.hao.heji.sync.handler

import com.hao.heji.App
import com.hao.heji.data.db.Book
import com.hao.heji.moshi
import com.hao.heji.proto.Message
import com.hao.heji.sync.convertToAck
import com.hao.heji.sync.toBytes
import okhttp3.WebSocket

class AddBookHandler : IMessageHandler {
    override fun canHandle(packet: Message.Packet): Boolean {
        val add = packet.type == Message.Type.ADD_BOOK
        val ack = packet.type == Message.Type.ADD_BOOK_ACK
        return add || ack
    }

    override fun handleMessage(webSocket: WebSocket, packet: Message.Packet) {
        if (packet.type == Message.Type.ADD_BOOK) {
            val book = moshi.adapter(Book::class.java).fromJson(packet.content)
            book?.let {
                book.synced=1
                App.dataBase.bookDao().insert(book)
                //ACK
                val ack = packet.convertToAck(Message.Type.ADD_BOOK_ACK, book.id)
                webSocket.send(ack.toBytes())
            }
        }
        if (packet.type == Message.Type.ADD_BOOK_ACK) {
            App.dataBase.bookDao().updateSyncStatus(packet.content)
        }
    }
}

class DeleteBookHandler : IMessageHandler {
    override fun canHandle(packet: Message.Packet): Boolean {
        val add = packet.type == Message.Type.DELETE_BOOK
        val ack = packet.type == Message.Type.DELETE_BOOK_ACK
        return add || ack
    }

    override fun handleMessage(webSocket: WebSocket, packet: Message.Packet) {
        if (packet.type == Message.Type.DELETE_BOOK) {
            val bookId = packet.content
            App.dataBase.bookDao().deleteById(bookId)
            val ack = packet.convertToAck(
                Message.Type.DELETE_BOOK_ACK,
                bookId,
            )
            webSocket.send(ack.toBytes())
        }
        if (packet.type == Message.Type.DELETE_BOOK_ACK) {
            val bookId = packet.content
            App.dataBase.bookDao().deleteById(bookId)
        }
    }
}

class UpdateBookHandler : IMessageHandler {
    override fun canHandle(packet: Message.Packet): Boolean {
        val add = packet.type == Message.Type.UPDATE_BOOK
        val ack = packet.type == Message.Type.UPDATE_BOOK_ACK
        return add || ack
    }

    override fun handleMessage(webSocket: WebSocket, packet: Message.Packet) {
        if (packet.type == Message.Type.UPDATE_BOOK) {
            val book = moshi.adapter(Book::class.java).fromJson(packet.content)
            book?.let {
                App.dataBase.bookDao().insert(book)
                val ack = packet.convertToAck(
                    Message.Type.UPDATE_BOOK_ACK,
                    book.id,
                )
                webSocket.send(ack.toBytes())
            }
        }
        if (packet.type == Message.Type.UPDATE_BOOK_ACK) {
            App.dataBase.bookDao().updateSyncStatus(packet.content)
        }
    }
}