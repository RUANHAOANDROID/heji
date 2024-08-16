package com.hao.heji.service.ws

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hao.heji.sync.WebSocketClient
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncWebSocketTest {
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoi6Ziu55qTIiwiaWQiOiIxNTg3Mjc0NDMzNSIsImV4cCI6MTcxNDg5NjkwMH0.qv7W2EyB2DmApdBkkSn1AqrxObuaru5aZ7AQ2BhuQas"

    @Test
    fun testWebSocket() {
        val instance = WebSocketClient.getInstance()
        instance.connect(wsUrl = "ws://192.168.8.68:8888/api/v1/ws", token)
        Thread.sleep(3000)
    }
}