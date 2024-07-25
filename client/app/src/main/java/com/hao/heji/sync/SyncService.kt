package com.hao.heji.sync

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.config.Config
import com.hao.heji.sync.impl.BillSyncImpl
import com.hao.heji.sync.impl.BookSyncImpl
import com.hao.heji.service.ws.SyncPusher
import com.hao.heji.service.ws.SyncWebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * 同步服务
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class SyncService : Service() {
    private lateinit var mBookSync: IBookSync
    private lateinit var mBillSync: IBillSync
    private val binder = SyncBinder()

    //后台具体操作任务使用server scope
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var syncPusher: SyncPusher? = null
    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
        mBookSync = BookSyncImpl(scope)
        mBillSync = BillSyncImpl(scope)
        val address = Config.serverUrl.split("://")[1]
        val token = Config.user.token
        val instance = SyncWebSocket.getInstance()
        instance
            .connect(wsUrl = "ws://${address}/api/v1/ws", token, scope)
        syncPusher= SyncPusher(instance)
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

    fun getBookSyncManager(): IBookSync = mBookSync

    fun getBillSyncManager(): IBillSync = mBillSync
    fun getSyncPusher(): SyncPusher? = syncPusher
}