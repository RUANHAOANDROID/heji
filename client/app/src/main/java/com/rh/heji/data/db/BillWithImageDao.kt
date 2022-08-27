package com.rh.heji.data.db

import androidx.room.Dao
import androidx.room.Transaction
import com.rh.heji.App

@Dao
abstract class BillWithImageDao {
    @Transaction
    open suspend fun installBillAndImage(bill: Bill, images: MutableList<Image>): Long {
        val count = App.dataBase.billDao().install(bill)
        if (images.isNotEmpty() && images.size > 0)
            App.dataBase.imageDao().install(images)
        return count
    }

    @Transaction
    open suspend fun findBillAndImage(bill_id: String): Bill {
        val bill = App.dataBase.billDao().findById(bill_id)
        if (bill != null) {
            App.dataBase.imageDao().findByBillId(bill_id)
        }
        return bill
    }
}