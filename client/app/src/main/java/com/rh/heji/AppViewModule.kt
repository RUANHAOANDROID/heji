package com.rh.heji

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.CRUD
import com.rh.heji.data.DBObservable
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Dealer
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.service.work.DataSyncWork
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import com.rh.heji.utlis.mmkv

class AppViewModule(application: Application) : AndroidViewModel(application) {
    val network: HejiNetwork = HejiNetwork.getInstance()
    private val billRepository = BillRepository()
    private val categoryRepository = CategoryRepository()
    val asyncLiveData = MediatorLiveData<Any>()

    val dbObservable = MediatorLiveData<DBObservable>()



    init {
        launchIO({
            LogUtils.getConfig().globalTag = "TAG"
        }, {
            it.printStackTrace()
        })

    }


    fun billPush(bill: Bill) {
        launchIO({ billRepository.pushBill(bill) })

    }

    fun billDelete(bill: Bill) {
        launchIO({
            AppDatabase.getInstance().billDao().preDelete(bill.id)
            dbObservable.postValue(DBObservable(CRUD.DELETE, bill))
            billRepository.deleteBill(bill.id)
        })

    }

    fun billUpdate(bill: Bill) {
        launchIO({ billRepository.updateBill(bill) })
    }

    fun billPull() {
        currentYearMonth.let {
            val currentLastDay = MyTimeUtils.getMonthLastDay(it.year, it.month);
            val statDate = YearMonth(it.year, it.month, 1).toYearMonthDay()
            val endDate = YearMonth(it.year, it.month, currentLastDay).toYearMonthDay()
            launchIO({ billRepository.pullBill(statDate, endDate) })
        }


    }

    fun categoryPush(categoryEntity: CategoryEntity) {
        launchIO({ categoryRepository.pushCategory(categoryEntity) })
    }

    fun categoryPull() {
        launchIO({ categoryRepository.pullCategory() })
    }

    fun asyncData() {
        launchIO({
            var dataAsyncWork = DataSyncWork()
            dataAsyncWork.asyncBooks()
            //DataSyncWork 方法执行在IO线程
            dataAsyncWork.asyncBills()
            dataAsyncWork.asyncCategory()
            asyncLiveData.postValue("OJBK")
        }, {
            it.printStackTrace()
        })

    }

    override fun onCleared() {
        super.onCleared()
    }

    companion object {
        private var instance: AppViewModule? = null
        fun get(context: Context = App.context()): AppViewModule =
            instance ?: synchronized(this) {
                instance ?: AppViewModule(context as Application)
                    .also { instance = it }
            }
    }
}