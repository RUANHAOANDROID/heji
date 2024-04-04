package com.hao.heji.service.ws.handlers

interface MessageHandler {
    fun handleMessage(message: String): Boolean
}
// 责任链节点
class MessageHandlerNode(private val handler: MessageHandler) {
    private var nextNode: MessageHandlerNode? = null

    fun setNext(node: MessageHandlerNode) {
        nextNode = node
    }

    fun handle(message: String): Boolean {
        return if (handler.handleMessage(message)) {
            true
        } else {
            nextNode?.handle(message) ?: false
        }
    }
}
