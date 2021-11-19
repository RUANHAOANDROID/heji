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
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.service.work.DataSyncWork
import com.rh.heji.utlis.launchIO

class AppViewModule(application: Application) : AndroidViewModel(application) {
    private val billRepository = BillRepository()

    val asyncLiveData = MediatorLiveData<Any>()

    val dbObservable = MediatorLiveData<DBObservable>()

    val loginEvent = MediatorLiveData<Event<Any>>()


    init {
        launchIO({
            LogUtils.getConfig().globalTag = "TAG"
        }, {
            it.printStackTrace()
        })

    }


    fun billDelete(bill: Bill) {
        launchIO({
            val status = AppDatabase.getInstance().billDao().status(bill.id)
            when (status) {
                STATUS.NOT_SYNCED -> {//未同步直接删除
                    AppDatabase.getInstance().billDao().deleteById(bill.id)
                    dbObservable.postValue(DBObservable(CRUD.DELETE, bill))
                    return@launchIO
                }
                else -> {
                    AppDatabase.getInstance().billDao().preDelete(bill.id)
                }
            }
            dbObservable.postValue(DBObservable(CRUD.DELETE, bill))
            billRepository.deleteBill(bill.id)
        })

    }

    fun asyncData() {
        launchIO({
            var dataAsyncWork = DataSyncWork()
            dataAsyncWork.syncByOperateLog()
            dataAsyncWork.syncBooks()
            //DataSyncWork 方法执行在IO线程
            dataAsyncWork.syncBills()
            dataAsyncWork.syncCategory()
            asyncLiveData.postValue("OJBK")//同步完成通知刷新
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