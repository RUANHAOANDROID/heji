package com.hao.heji.service.ws

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hao.heji.App
import com.hao.heji.data.db.Bill
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class SyncWebSocketTest {
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoi6Ziu55qTIiwiaWQiOiIxNTg3Mjc0NDMzNSIsImV4cCI6MTcxNDg5NjkwMH0.qv7W2EyB2DmApdBkkSn1AqrxObuaru5aZ7AQ2BhuQas"

    @Test
    fun testWebSocket() {
        val instance = SyncWebSocket.getInstance()
        instance.connect(wsUrl = "ws://192.168.8.68:8888/api/v1/ws", token, App.viewModel.viewModelScope)
        Thread.sleep(3000)
        val pusher = MessagePusher()
        pusher.addBill(Bill().apply {
            bookId="6605416bb83e8964d46add39"
            money= BigDecimal.ONE
            type=1
            category="test"
            createUser="hao88"
        })
    }
}