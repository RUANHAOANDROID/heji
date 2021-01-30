package com.rh.heji

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Dealer
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.network.HejiNetwork
import kotlinx.coroutines.launch

class AppViewModule(application: Application) : AndroidViewModel(application) {
    val network: HejiNetwork = HejiNetwork.getInstance()

    init {
        launch({
            fakeData()
        }, {
            it.printStackTrace()
        })
    }
    
    override fun onCleared() {
        super.onCleared()
    }

    fun launch(block: suspend () -> Unit, error: suspend (Throwable) -> Unit) = viewModelScope.launch {
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
        val c1 = Category(ObjectId().toString(),"加气", 0, -1)
        val c2 = Category(ObjectId().toString(),"修理", 0, -1)
        val c3 = Category(ObjectId().toString(),"过路费", 0, -1)
        val c4 = Category(ObjectId().toString(),"罚款", 0, -1)
        val c5 = Category(ObjectId().toString(),"保险", 0, -1)
        val c6 = Category(ObjectId().toString(),"矿石", 0, 1)

//        AppDatabase.getInstance().categoryDao().insert(c1)
//        AppDatabase.getInstance().categoryDao().insert(c2)
//        AppDatabase.getInstance().categoryDao().insert(c3)
//        AppDatabase.getInstance().categoryDao().insert(c4)
//        AppDatabase.getInstance().categoryDao().insert(c5)
//        AppDatabase.getInstance().categoryDao().insert(c6)
    }
}