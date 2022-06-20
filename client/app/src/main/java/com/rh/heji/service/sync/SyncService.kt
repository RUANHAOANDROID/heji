package com.rh.heji.service.sync

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mBookSync = BookSyncImpl(scope)
        mBillSync = BillSyncImpl(scope)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    inner class SyncBinder : Binder() {
        fun getService(): SyncService = this@SyncService
    }

    fun getBookSyncManager(): IBookSync = mBookSync

    fun getBillSyncManager(): IBillSync = mBillSync
}