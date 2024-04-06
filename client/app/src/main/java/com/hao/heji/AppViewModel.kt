package com.hao.heji

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.service.ws.MessagePusher
import com.hao.heji.service.ws.SyncWebSocket
import java.math.BigDecimal

class AppViewModel(application: App) : AndroidViewModel(application) {
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoi6Ziu55qTIiwiaWQiOiIxNTg3Mjc0NDMzNSIsImV4cCI6MTcxNDk3ODUzNH0.x50cjB8vIg7LjrGUb_zm0gRJScdKwSGvMaYzYl-1Gac"

    val loginEvent = MediatorLiveData<Event<Any>>()
    fun connectServer(){
        val instance = SyncWebSocket.getInstance()
        instance.connect(wsUrl = "ws://192.168.8.68:8888/api/v1/ws", token, App.viewModel.viewModelScope)
        Thread.sleep(2000)
        val pusher = MessagePusher()
        val bill = Bill().apply {
            bookId = ObjectId().toHexString()
            money = BigDecimal.ONE
            type = 1
            category = "test"
            createUser = "hao88"
            remark = "测试推送账单"
        }
        pusher.addBill(bill)
    }
}