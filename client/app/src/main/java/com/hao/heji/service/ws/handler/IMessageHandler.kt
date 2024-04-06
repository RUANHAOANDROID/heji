package com.hao.heji.service.ws.handler

import com.hao.heji.proto.Message

interface IMessageHandler {
    fun handleMessage(packet: Message.Packet)
    fun setNextHandler(handler: IMessageHandler): IMessageHandler
}