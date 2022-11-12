package com.rh.heji

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.service.work.DataSyncWork
import com.rh.heji.utlis.launchIO

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val loginEvent = MediatorLiveData<Event<Any>>()

    init {
        launchIO({
            LogUtils.getConfig().globalTag = "TAG"
        }, {
            it.printStackTrace()
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