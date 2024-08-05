package com.hao.heji.sync

import NetworkMonitor
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 同步服务
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class SyncService : Service(), Observer<Config> {
    private val binder = SyncBinder()

    //后台具体操作任务使用server scope
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val syncWebSocket = SyncWebSocket.getInstance()
    private val syncTrigger = SyncTrigger(syncWebSocket, scope)
    private var networkMonitor: NetworkMonitor? = null
    private val configLiveData = App.viewModel.configChange.asLiveData()
    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
        // 监听网络变化
        networkMonitor = NetworkMonitor(this) {
            if (it) {
                if (syncWebSocket.isClose() || syncWebSocket.isError()) {
                    connectSync()
                }
            }
        }
        networkMonitor?.startNetworkCallback()
        configLiveData.observeForever(this)
    }

    private fun connectSync() {
        val address = Config.serverUrl.split("://")[1]
        val token = Config.user.token
        syncWebSocket.connect(wsUrl = "ws://${address}/api/v1/ws", token, scope)
        syncTrigger.register()
    }

    private fun closeSync() {
        syncTrigger.unregister()
        syncWebSocket.close()
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
        closeSync()
        job.cancel()
        scope.cancel()
        networkMonitor?.stopNetworkCallback()
        configLiveData.removeObserver(this)
    }

    inner class SyncBinder : Binder() {
        fun getService(): SyncService = this@SyncService
    }

    override fun onChanged(value: Config) {
        if (syncWebSocket.isClose()) {
            connectSync()
        }
    }
}