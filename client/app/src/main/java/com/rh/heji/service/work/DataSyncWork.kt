package com.rh.heji.service.work

import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Constant
import com.rh.heji.data.db.Image
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.BillEntity
import com.rh.heji.network.request.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSyncWork {
    private val billDao = AppDatabase.getInstance().billDao()
    private val categoryDao = AppDatabase.getInstance().categoryDao()
    private val network = HejiNetwork.getInstance()
    private val billRepository = BillRepository()
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
            if (updateCategory.isNotEmpty()) {
                updateCategory.forEach { category ->
                    val response = network.categoryPush(CategoryEntity(category))
                    if (response.code == 0) {
                        category.synced = Constant.STATUS_SYNCED
                        categoryDao.update(category)
                    }
                }
            }
    }

    private suspend fun categoryPush() {
        val pushCategory = categoryDao.findCategoryByStatic(Constant.STATUS_UPDATE)
            if (pushCategory.isNotEmpty()) {
                pushCategory.forEach { category ->
                    val response = network.categoryPush(CategoryEntity(category))
                    if (response.code == 0) {
                        category.synced = Constant.STATUS_SYNCED
                        categoryDao.update(category)
                    }
                }
            }
    }

    private suspend fun categoryPull() {
        val categoryResponse = network.categoryPull()
        if (categoryResponse.code == 0) {
            val data = categoryResponse.data
            if (data.isNotEmpty()) {
                data.forEach { categoryEntity ->
                    categoryDao.insert(categoryEntity.toDbCategory())
                }
            }
        }
    }

    private suspend fun categoryDelete() {
        val deleteCategory = categoryDao.findCategoryByStatic(Constant.STATUS_DELETE)
        if (deleteCategory.isNotEmpty()) {
            deleteCategory.forEach { category ->
                val response = network.categoryDelete(category.id)
                if (response.code == 0) {
                    categoryDao.delete(category)
                }
            }
        }
    }

    private suspend fun billDelete() {
        val deleteBills = billDao.findByStatus(Constant.STATUS_DELETE)
        if (deleteBills.isNotEmpty()) {
            deleteBills.forEach { bill ->
                var response = network.billDelete(bill.id)
                if (response.code == 0) {
                    billDao.delete(bill)
                }
            }
        }
    }

    private suspend fun billsUpdate() {
        val updateBills = billDao.findByStatus(Constant.STATUS_UPDATE)
        if (updateBills.isNotEmpty()) {
            updateBills.forEach { bill ->
                val response = network.billUpdate(BillEntity(bill))
                if (response.code == 0) {
                    bill.synced = Constant.STATUS_SYNCED
                    AppDatabase.getInstance().imageDao().deleteBillImage(bill.id)
                    billDao.delete(bill)
                }
            }
        }
    }

    private suspend fun billsPush() {
        val pushBills = billDao.findByStatus(Constant.STATUS_NOT_SYNC)
        if (pushBills.isNotEmpty()) {
            pushBills.forEach { bill ->
                billRepository.pushBill(BillEntity(bill))
            }
        }
    }

    private suspend fun billsPull() {
        val pullBillsResponse = network.billPull("0", "0")
        var data = pullBillsResponse.data
        data?.let { serverBills ->
            if (serverBills.isNotEmpty()) {
                serverBills.forEach { serverBill ->
                    val existCount = billDao.countById(serverBill.id)//本地的

                    if (existCount == 0) {//不存在直接存入
                        billDao.install(serverBill.toBill())
                    }
                    var imagesId = serverBill.images//云图片
                    if (null != imagesId && imagesId.size > 0) {//有图片
                        var response = network.billPullImages(serverBill.id)
                        response.date?.forEach { entity ->
                            var image = Image(entity._id, billImageID = serverBill.id)
                            image.id = entity._id
                            image.md5 = entity.md5
                            image.onlinePath = entity._id.toString()
                            image.ext = entity.ext
                            image.billImageID = serverBill.id
                            image.synced = Constant.STATUS_SYNCED
                            AppDatabase.getInstance().imageDao().install(image)
                            LogUtils.i("账单图片信息已保存 $image")
                        }
                        billDao.updateImageCount(response.date.size, serverBill.id)
                    }
                }
            }
        }

    }

}