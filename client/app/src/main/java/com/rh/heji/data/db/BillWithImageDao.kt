package com.rh.heji.data.db

import androidx.room.Dao
import androidx.room.Transaction
import com.rh.heji.data.AppDatabase

@Dao
abstract class BillWithImageDao {
    @Transaction
    open suspend fun installBillAndDao(bill: Bill, images: MutableList<Image>): Long {
        val count = AppDatabase.getInstance().billDao().install(bill)
        if (images.isNotEmpty()&&images.size > 0)
            AppDatabase.getInstance().imageDao().install(images)
        return count
    }
}