package com.rh.heji.service.work

import com.blankj.utilcode.util.LogUtils
import com.rh.heji.currentUser
import com.rh.heji.currentYearMonth
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.security.Token
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSyncWork {
    private val network = HejiNetwork.getInstance()
    private val bookDao = AppDatabase.getInstance().bookDao()
    private val billDao = AppDatabase.getInstance().billDao()
    private val categoryDao = AppDatabase.getInstance().categoryDao()
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
        val updateCategory = categoryDao.findCategoryByStatic(STATUS.UPDATED)
        if (updateCategory.isNotEmpty()) {
            updateCategory.forEach { category ->
                val response = network.categoryPush(CategoryEntity(category))
                if (response.code == 0) {
                    category.synced = STATUS.SYNCED
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
                    category.synced = STATUS.SYNCED
                    categoryDao.update(category)
                }
            }
        }
    }

    private suspend fun categoryPull() {
        val categoryResponse = network.categoryPull()
        if (categoryResponse.code == 0) {
            val data = categoryResponse.date
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

    private suspend fun billDelete() {
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

    private suspend fun billsUpdate() {
        val updateBills = billDao.findByStatus(STATUS.UPDATED)
        if (updateBills.isNotEmpty()) {
            updateBills.forEach { bill ->
                val response = network.billUpdate(bill)
                if (response.code == 0) {
                    bill.synced = STATUS.SYNCED
                    AppDatabase.getInstance().imageDao().deleteBillImage(bill.id)
                    billDao.delete(bill)
                }
            }
        }
    }

    private suspend fun billsPush() {
        val pushBills = billDao.findByStatus(STATUS.NOT_SYNCED)
        if (pushBills.isNotEmpty()) {
            pushBills.forEach { bill ->
                billRepository.pushBill(bill)
            }
        }
    }

    private suspend fun billsPull() {
        val currentLastDay =
            MyTimeUtils.getMonthLastDay(currentYearMonth.year, currentYearMonth.month);
        val statDate = YearMonth(currentYearMonth.year, currentYearMonth.month, 1).toYearMonthDay()
        val endDate = YearMonth(
            currentYearMonth.year,
            currentYearMonth.month,
            currentLastDay
        ).toYearMonthDay()
        val pullBillsResponse = network.billPull(statDate, endDate)
        var data = pullBillsResponse.date
        data?.let { serverBills ->
            if (serverBills.isNotEmpty()) {
                serverBills.forEach { serverBill ->
                    val existCount = billDao.countById(serverBill.id)//本地的

                    if (existCount == 0) {//不存在直接存入
                        billDao.install(serverBill)
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
                            image.synced = STATUS.SYNCED
                            AppDatabase.getInstance().imageDao().install(image)
                            LogUtils.d("账单图片信息已保存 $image")
                        }
                        billDao.updateImageCount(response.date.size, serverBill.id)
                    }
                }
            }
        }

    }

    suspend fun asyncBooks() {
        network.bookPull().let {
            if (it.code == 0) {
                it.date.forEach { book ->
                    val localBoos = bookDao.findBook(book.id)
                    if (localBoos.isEmpty()) {
                        bookDao.insert(book)
                    } else {
                        bookDao.update(book)
                    }
                }
            }
        }
        val notAsyncBooks = bookDao.books(STATUS.NOT_SYNCED)
        for (book in notAsyncBooks) {
            book.synced = STATUS.SYNCED
            book.createUser = JWTParse.getUser(Token.decodeToken()).username
            val response = network.bookPush(book)
            if (response.code == 0) {
                val count = bookDao.update(book)
                if (count > 0) {
                    LogUtils.d("本地账本同步成功{$book}")
                }
            }
        }

    }

}