package com.hao.heji.service.work

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.currentYearMonth
import com.hao.heji.App
import com.hao.heji.data.db.Image
import com.hao.heji.data.db.STATUS
import com.hao.heji.data.repository.BillRepository
import com.hao.heji.network.HttpManager
import com.hao.heji.network.request.CategoryEntity
import com.hao.heji.utils.MyTimeUtils
import com.hao.heji.utils.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSyncWork {
    private val network = HttpManager.getInstance()
    private val bookDao = App.dataBase.bookDao()
    private val billDao = App.dataBase.billDao()
    private val categoryDao = App.dataBase.categoryDao()
    private val billRepository = BillRepository()
    suspend fun syncBills() {
        suspend fun delete() {
            val deleteBills = billDao.findByStatus(STATUS.DELETED)
            if (deleteBills.isNotEmpty()) {
                deleteBills.forEach { bill ->
                    var response = network.deleteBill(bill.id)
                    if (response.code == 0) {
                        billDao.delete(bill)
                    }
                }
            }
        }

        suspend fun update() {
            val updateBills = billDao.findByStatus(STATUS.UPDATED)
            if (updateBills.isNotEmpty()) {
                updateBills.forEach { bill ->
                    val response = network.updateBill(bill)
                    if (response.code == 0) {
                        bill.syncStatus = STATUS.SYNCED
                        App.dataBase.imageDao().deleteBillImage(bill.id)
                        billDao.delete(bill)
                    }
                }
            }
        }

        suspend fun pull() {
            //TODO 目前仅拉取了当月 ，后期启动拉取整个账本数据
            val currentLastDay =
                MyTimeUtils.getMonthLastDay(currentYearMonth.year, currentYearMonth.month)
            val statDate =
                YearMonth(currentYearMonth.year, currentYearMonth.month, 1).yearMonthDayString()
            val endDate = YearMonth(
                currentYearMonth.year,
                currentYearMonth.month,
                currentLastDay
            ).yearMonthDayString()
            val pullBillsResponse = network.pullBill(statDate, endDate)
            var data = pullBillsResponse.data
            data?.let { serverBills ->
                if (serverBills.isNotEmpty()) {
                    serverBills.forEach { serverBill ->
                        serverBill.syncStatus =STATUS.SYNCED //拉取的账本为已同步账单
                        val existCount = billDao.countById(serverBill.id)//本地的

                        if (existCount == 0) {//不存在直接存入
                            billDao.install(serverBill)
                        }
                        var imagesId = serverBill.images//云图片
                        if (imagesId.isNotEmpty()) {//有图片
                            var response = network.imageDownload(serverBill.id)
                            response.data?.forEach { entity ->
                                var image = Image(entity._id, billID = serverBill.id)
                                image.id = entity._id
                                image.md5 = entity.md5
                                image.onlinePath = entity._id
                                image.ext = entity.ext
                                image.billID = serverBill.id
                                image.syncStatus = STATUS.SYNCED
                                App.dataBase.imageDao().install(image)
                                LogUtils.d("账单图片信息已保存 $image")
                            }
                        }
                    }
                }
            }

        }
        withContext(Dispatchers.IO) {
            delete()
            update()
            pull()
        }
    }

    suspend fun syncCategory() {
        withContext(Dispatchers.IO) {
            categoryDelete()
            categoryUpdate()
            categoryPush()
            categoryPull()
        }
    }

    private suspend fun categoryUpdate() {
        val updateCategory = categoryDao.findCategoryByStatic(STATUS.UPDATED)
        if (updateCategory.isNotEmpty()) {
            updateCategory.forEach { category ->
                val response = network.categoryPush(CategoryEntity(category))
                if (response.code == 0) {
                    category.syncStatus = STATUS.SYNCED
                    categoryDao.update(category)
                }
            }
        }
    }

    private suspend fun categoryPush() {
        val pushCategory = categoryDao.findCategoryByStatic(STATUS.UPDATED)
        if (pushCategory.isNotEmpty()) {
            pushCategory.forEach { category ->
                val response = network.categoryPush(CategoryEntity(category))
                if (response.code == 0) {
                    category.syncStatus = STATUS.SYNCED
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
        val deleteCategory = categoryDao.findCategoryByStatic(STATUS.DELETED)
        if (deleteCategory.isNotEmpty()) {
            deleteCategory.forEach { category ->
                val response = network.categoryDelete(category.id)
                if (response.code == 0) {
                    categoryDao.delete(category)
                }
            }
        }
    }
}