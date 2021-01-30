package com.rh.heji.data.repository

import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Constant
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.BillEntity
import java.util.stream.Collectors

object BillRepository {
    val hejiNetwork = HejiNetwork.getInstance()
    var billDao = AppDatabase.INSTANCE.billDao()
    suspend fun saveBill(billEntity: BillEntity) {
        val response = hejiNetwork.billPush(billEntity)
        response.data.let {
            var bill = billEntity.toBill()
            bill.synced = Constant.STATUS_SYNCED
            billDao.update(Bill())
        }
    }

    suspend fun deleteBill(_id: String) {
        var response = hejiNetwork.billDelete(_id)
        response.data.let {
            billDao.delete(Bill(_id))
        }
    }

    suspend fun updateBill(billEntity: BillEntity) {
        var response = hejiNetwork.billUpdate(billEntity)
        response.data.let {
            billDao.update(billEntity.toBill())
        }
    }

    suspend fun pullBill(startTime: String = "0", endTime: String = "0") {
        var response = hejiNetwork.billPull(startTime, endTime)
        response.data.let {
            if (it.isNotEmpty()) {
                it.stream().forEach { entity ->
                    billDao.install(entity.toBill())
                }
            }
        }
    }
}