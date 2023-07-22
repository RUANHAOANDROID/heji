package com.rh.heji.service.work

import com.blankj.utilcode.util.LogUtils
import com.rh.heji.currentYearMonth
import com.rh.heji.App
import com.rh.heji.Config
import com.rh.heji.store.DataStoreManager
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.network.HttpManager
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.network.response.OperateLog
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utils.MyTimeUtils
import com.rh.heji.utils.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DataSyncWork {
    private val network = HttpManager.getInstance()
    private val bookDao = App.dataBase.bookDao()
    private val billDao = App.dataBase.billDao()
    private val categoryDao = App.dataBase.categoryDao()
    private val billRepository = BillRepository()
    suspend fun syncByOperateLog() {
        /**
         * 根据服务器账本删除日志，同步删除本地数据
         */
        val response = HttpManager.getInstance().bookOperateLogs( Config.book.id)
        if (response.code == 0 && response.data.isNotEmpty()) {
            val operates = response.data
            for (operate in operates) {
                when (operate.opeClass) {
                    OperateLog.BOOK -> {
                        if (operate.opeType == OperateLog.DELETE) {
                            bookDao.deleteById(operate.bookId)
                        }
                    }
                    OperateLog.BILL -> {
                        if (operate.opeType == OperateLog.DELETE) {
                            billDao.deleteById(operate.opeID)
                        }
                    }
                    OperateLog.CATEGORY -> {
                        if (operate.opeType == OperateLog.DELETE) {
                            categoryDao.deleteById(operate.opeID)
                        }
                    }
                }
            }
        }
    }

    suspend fun syncBills() {
        suspend fun delete() {
            val deleteBills = billDao.findByStatus(STATUS.DELETED)
            if (deleteBills.isNotEmpty()) {
                deleteBills.forEach { bill ->
                    var response = network.billDelete(bill.id)
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
                    val response = network.billUpdate(bill)
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
            val pullBillsResponse = network.billPull(statDate, endDate)
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


    suspend fun syncBooks() {
        network.bookPull().let {
            if (it.code == 0) {
                it.data.forEach { netBook ->
                    val localBoos = bookDao.findBook(netBook.id)
                    netBook.syncStatus = STATUS.SYNCED
                    if (localBoos.isEmpty()) {
                        bookDao.insert(netBook)
                    } else {
                        bookDao.update(netBook)
                    }
                }
            }
        }
        val notAsyncBooks = bookDao.books(STATUS.NOT_SYNCED)//未上传同步的账本
        for (book in notAsyncBooks) {
            book.syncStatus = STATUS.SYNCED
            book.createUser = JWTParse.getUser(DataStoreManager.getToken().first() ?: "").name
            val response = network.bookCreate(book)
            if (response.code == 0) {
                val count = bookDao.update(book)
                if (count > 0) {
                    LogUtils.d("本地账本同步成功{$book}")
                }
            }
        }

    }

}