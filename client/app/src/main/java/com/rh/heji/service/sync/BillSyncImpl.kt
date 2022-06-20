package com.rh.heji.service.sync

import com.rh.heji.App
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.network.HejiNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 账单同步实现
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class BillSyncImpl(private val scope: CoroutineScope) : IBillSync {
    override fun compare() {

    }

    override fun delete(bookID: String) {
        scope.launch {
            val response = HejiNetwork.getInstance().bookDelete(bookID)
            if (response.success()) {
                App.dataBase.bookDao().deleteById(response.data)
            }
        }
    }

    override fun add(bill: Bill) {
        scope.launch {
            val response = HejiNetwork.getInstance().billPush(bill)
            if (response.success()) {
                App.dataBase.billDao().upsert(bill.apply {
                    synced = STATUS.SYNCED
                })
            }
        }
    }

    override fun update(bill: Bill) {
        scope.launch {
            val response = HejiNetwork.getInstance()
                .billUpdate(bill)
            if (response.success()) {
                App.dataBase.billDao().update(bill.apply {
                    synced = STATUS.SYNCED
                })
            }
        }
    }
}