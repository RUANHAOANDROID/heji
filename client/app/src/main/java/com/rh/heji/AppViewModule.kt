package com.rh.heji

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.TimeUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Dealer
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.BillEntity
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.service.work.DataSyncWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModule(application: Application) : AndroidViewModel(application) {
    val network: HejiNetwork = HejiNetwork.getInstance()
    private val billRepository = BillRepository()
    private val categoryRepository = CategoryRepository()

    init {
        launch({
            fakeData()
        }, {
            it.printStackTrace()
        })
    }

    fun billPush(billEntity: BillEntity) {
        launch({ billRepository.pushBill(billEntity) })

    }

    fun billDelete(_id: String) {
        launch({ billRepository.deleteBill(_id) })

    }

    fun billUpdate(billEntity: BillEntity) {
        launch({ billRepository.updateBill(billEntity) })
    }

    fun billPull() {
        launch({ billRepository.pullBill() })
    }

    fun categoryPush(categoryEntity: CategoryEntity) {
        launch({ categoryRepository.pushCategory(categoryEntity) })
    }

    fun categoryPull() {
        launch({ categoryRepository.pullCategory() })
    }

    fun asyncData() {
        launch({
            var dataAsyncWork = DataSyncWork()
            //DataSyncWork 方法执行在IO线程
            dataAsyncWork.asyncBills()
            dataAsyncWork.asyncCategory()
        }, {
            it.printStackTrace()
        })

    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun launch(block: suspend () -> Unit, error: suspend (Throwable) -> Unit = { it.printStackTrace() }) = viewModelScope.launch(Dispatchers.IO) {
        try {
            block()
        } catch (e: Throwable) {
            error(e)
        }
    }

    fun fakeData() {
        val u1 = Dealer("司机")
        val u2 = Dealer("祝")
        val u3 = Dealer("皓")
        val u4 = Dealer("孔")
        AppDatabase.getInstance().dealerDao().insert(u1)
        AppDatabase.getInstance().dealerDao().insert(u2)
        AppDatabase.getInstance().dealerDao().insert(u3)
        AppDatabase.getInstance().dealerDao().insert(u4)

        val startCount = AppCache.instance.kvStorage?.decodeInt("start", 0)
        if (startCount == 1) {
            val c0_0 = Category(ObjectId().toString(), "其他", 0, -1)
            val c0_1 = Category(ObjectId().toString(), "其他", 0, 1)
            AppDatabase.getInstance().categoryDao().insert(c0_0)
            AppDatabase.getInstance().categoryDao().insert(c0_1)
        }
        if (startCount == 1) {
            val c1 = Category(ObjectId().toString(), "加气", 0, -1)
            val c2 = Category(ObjectId().toString(), "修理", 0, -1)
            val c3 = Category(ObjectId().toString(), "过路费", 0, -1)
            val c4 = Category(ObjectId().toString(), "罚款", 0, -1)
            val c5 = Category(ObjectId().toString(), "保险", 0, -1)
            val c6 = Category(ObjectId().toString(), "货运", 0, 1)

            AppDatabase.getInstance().categoryDao().insert(c5)
            AppDatabase.getInstance().categoryDao().insert(c4)
            AppDatabase.getInstance().categoryDao().insert(c3)
            AppDatabase.getInstance().categoryDao().insert(c2)
            AppDatabase.getInstance().categoryDao().insert(c1)

            AppDatabase.getInstance().categoryDao().insert(c6)


        }
    }
}