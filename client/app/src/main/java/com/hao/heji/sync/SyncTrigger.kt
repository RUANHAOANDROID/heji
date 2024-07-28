package com.hao.heji.sync

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.STATUS.DELETED
import com.hao.heji.data.db.STATUS.NEW
import com.hao.heji.data.db.STATUS.UPDATED
import com.hao.heji.moshi
import com.hao.heji.proto.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 同步触发器
 * 1. 观察本地数据库数据（根据同步状态）
 * 2. 通过节点服务同步
 */
class SyncTrigger(private val syncWebSocket: SyncWebSocket, private val scope: CoroutineScope) {
    private val billDao = App.dataBase.billDao()
    private val bookDao = App.dataBase.bookDao()
    private val bookUserDao = App.dataBase.bookUserDao()
    private val config = com.hao.heji.config.Config
    private var isProcessing = false
    private val billJob = scope.launch {
        billDao.flowNotSynced(config.book.id).collect {
            if (isProcessing) {
                LogUtils.d("正在处理中...")
                LogUtils.d("本次触发不生效...")
                return@collect
            }
            if (it.size <= 0) {
                LogUtils.d("空的数据触发...")
                return@collect
            }
            withContext(Dispatchers.IO) {
                isProcessing = true
                LogUtils.d("开始处理账单 count=${it.size}...")
                for (bill in it) {
                    LogUtils.d(it)
                    when (bill.syncStatus) {
                        NEW -> {
                            LogUtils.d("同步...")
                            val json = moshi.adapter(Bill::class.java).toJson(bill)
                            val users = bookUserDao.findUsersId(bill.bookId)
                            syncWebSocket.send(
                                createPacket(
                                    Message.Type.ADD_BILL,
                                    content = json,
                                    toUsers = users
                                ).toBytes()
                            )
                        }

                        DELETED -> {
                            LogUtils.d("删除...")
                            val users = bookUserDao.findUsersId(bill.bookId)
                            syncWebSocket.send(
                                createPacket(
                                    Message.Type.DELETE_BILL,
                                    content = bill.id,
                                    toUsers = users
                                ).toBytes()
                            )
                        }

                        UPDATED -> {
                            LogUtils.d("更新...")
                            val json = moshi.adapter(Bill::class.java).toJson(bill)
                            val users = bookUserDao.findUsersId(bill.bookId)
                            syncWebSocket.send(
                                createPacket(
                                    Message.Type.UPDATE_BILL,
                                    content = json,
                                    toUsers = users
                                ).toBytes()
                            )
                        }
                    }
                }
                LogUtils.d("本次变更账单处理完成...")
                isProcessing = false
            }
        }
    }

    fun register() {
        if (!billJob.isActive) {
            billJob.start()
        }
    }

    fun unregister() {
        billJob.cancel()
    }

}