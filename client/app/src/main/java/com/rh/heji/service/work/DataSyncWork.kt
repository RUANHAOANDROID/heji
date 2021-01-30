package com.rh.heji.service.work

import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Constant
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.BillEntity
import com.rh.heji.network.request.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSyncWork {
    private val billDao = AppDatabase.INSTANCE.billDao()
    private val categoryDao = AppDatabase.INSTANCE.categoryDao()
    private val network = HejiNetwork.getInstance()
    suspend fun asyncBills() {
        withContext(Dispatchers.IO) {
            billDelete()
            billsUpdate()
            billsPush()
            billsPull()
        }
    }

    suspend fun asyncCategory() {
        withContext(Dispatchers.IO) {
            categoryDelete()

            categoryUpdate()

            categoryPush()

            categoryPull()
        }
    }

    private suspend fun categoryUpdate() {
        val updateCategory = categoryDao.findCategoryByStatic(Constant.STATUS_UPDATE)
        updateCategory?.let {
            if (it.size > 0) {
                it.forEach { category ->
                    category?.let {
                        val response = network.categoryPush(CategoryEntity(category))
                        if (response.code == 0) {
                            category.synced = Constant.STATUS_SYNCED
                            categoryDao.update(category)
                        }
                    }
                }
            }
        }
    }

    private suspend fun categoryPush() {
        val pushCategory = categoryDao.findCategoryByStatic(Constant.STATUS_UPDATE)
        pushCategory?.let {
            if (it.size > 0) {
                it.forEach { category ->
                    category?.let {
                        val response = network.categoryPush(CategoryEntity(category))
                        if (response.code == 0) {
                            category.synced = Constant.STATUS_SYNCED
                            categoryDao.update(category)
                        }
                    }
                }
            }
        }
    }

    private suspend fun categoryPull() {
        val categoryResponse = network.categoryPull()
        if (categoryResponse.code == 0) {
            val data = categoryResponse.data
            data?.let {
                if (it.isNotEmpty()) {
                    it.forEach { categoryEntity ->
                        categoryDao.insert(categoryEntity.toDbCategory())
                    }
                }
            }
        }
    }

    private suspend fun categoryDelete() {
        val deleteCategory = categoryDao.findCategoryByStatic(Constant.STATUS_DELETE)
        deleteCategory?.let {
            if (it.size > 0) {
                it.forEach { category ->
                    category?.let {
                        val response = network.categoryDelete(category._id)
                        if (response.code == 0) {
                            categoryDao.delete(category)
                        }
                    }
                }
            }
        }
    }

    private suspend fun billDelete() {
        val deleteBills = billDao.findBillsByStatus(Constant.STATUS_DELETE)
        deleteBills?.let {
            if (it.isNotEmpty() && it.size > 0) {
                it.forEach { bill ->
                    var response = network.billDelete(bill.id)
                    if (response.code == 0) {
                        billDao.delete(bill)
                    }
                }
            }
        }
    }

    private suspend fun billsUpdate() {
        val updateBills = billDao.findBillsByStatus(Constant.STATUS_UPDATE)
        updateBills?.let {
            if (it.isNotEmpty() && it.size > 0) {
                it.forEach { bill ->
                    val response = network.billUpdate(BillEntity(bill))
                    if (response.code == 0) {
                        bill.synced = Constant.STATUS_SYNCED
                        billDao.delete(bill)
                    }
                }
            }
        }
    }

    private suspend fun billsPush() {
        val pushBills = billDao.findBillsByStatus(Constant.STATUS_NOT_SYNC)
        pushBills?.let {
            if (it.isNotEmpty() && it.size > 0) {
                it.forEach { bill ->
                    var response = network.billPush(BillEntity(bill))
                    if (response.code == 0) {
                        bill.synced = Constant.STATUS_SYNCED
                        billDao.update(bill)
                    }
                }
            }
        }
    }

    private suspend fun billsPull() {
        val pullBillsResponse = network.billPull("0", "0")
        pullBillsResponse?.let {
            var data = it.data
            data?.let { serverBills ->
                if (serverBills.isNotEmpty()) {
                    serverBills.forEach { serverBill ->
                        val localBill = billDao.findByBillId(serverBill.id)
                        if (localBill.isEmpty())
                            billDao.install(serverBill.toBill())
                    }
                }
            }

        }
    }


}