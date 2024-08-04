package com.hao.heji.sync

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 同步服务
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class SyncService : Service() {
    private val binder = SyncBinder()
    //后台具体操作任务使用server scope
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val syncWebSocket = SyncWebSocket.getInstance()
    private val syncTrigger = SyncTrigger(syncWebSocket, scope)
    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
        connectSyncWebSocket()
        scope.launch {
            App.viewModel.configChange.collectLatest {
                connectSyncWebSocket()
            }
        }
    }

    private fun connectSyncWebSocket() {
        val address = Config.serverUrl.split("://")[1]
        val token = Config.user.token
        syncWebSocket.connect(wsUrl = "ws://${address}/api/v1/ws", token, scope)
        syncTrigger.register()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
        job.cancel()
        scope.cancel()
        SyncWebSocket.getInstance().close()
    }

    inner class SyncBinder : Binder() {
        fun getService(): SyncService = this@SyncService
    }
}