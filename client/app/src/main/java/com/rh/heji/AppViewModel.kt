package com.rh.heji

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.room.Entity
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.*
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.BookRepository
import com.rh.heji.service.work.DataSyncWork
import com.rh.heji.utlis.launchIO
import com.rh.heji.utlis.launchNewThread

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val billRepository = BillRepository()

    val asyncLiveData = MediatorLiveData<Any>()

    val localDataEvent = MediatorLiveData<EventMessage>()

    val loginEvent = MediatorLiveData<Event<Any>>()


    init {
        launchIO({
            LogUtils.getConfig().globalTag = "TAG"
        }, {
            it.printStackTrace()
        })
        localDataEvent.observeForever {
            val data = it.entity
            val optType = it.crud

            launchIO({
                if (data is Book) {
                    BookTask(optType, data).sync()
                }
                if (data is Bill) {
                    BillTask(optType, data).sync()
                }
                if (data is Category) {
                    CategoryTask(optType, data).sync()
                }
            }, { error ->
                error.printStackTrace()
            })
        }
    }


    fun billDelete(bill: Bill) {
        launchIO({
            val status = App.dataBase.billDao().status(bill.id)
            when (status) {
                STATUS.NOT_SYNCED -> {//未同步直接删除
                    App.dataBase.billDao().deleteById(bill.id)
                    DataBus.post(EventMessage(SyncEvent.DELETE, bill.copy()))
                    return@launchIO
                }
                else -> {
                    App.dataBase.billDao().preDelete(bill.id)
                }
            }
            DataBus.post(EventMessage(SyncEvent.DELETE, bill.copy()))
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

    companion object {
        private var instance: AppViewModel? = null
        fun get(context: Context = App.context): AppViewModel =
            instance ?: synchronized(this) {
                instance ?: AppViewModel(context as Application)
                    .also { instance = it }
            }
    }
}