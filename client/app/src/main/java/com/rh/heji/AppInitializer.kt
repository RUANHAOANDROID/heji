package com.rh.heji

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.StrictMode
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.startup.Initializer
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Dealer
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.network.HejiNetwork
import com.rh.heji.utlis.CrashInfo
import com.rh.heji.utlis.MyUtils
import com.rh.heji.utlis.http.basic.HttpRetrofit
import com.rh.heji.utlis.launchIO
import com.rh.heji.utlis.mmkv
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * startup.InitializationProvider
 */
class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {
//        if (BuildConfig.DEBUG) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork() // or .detectAll() for all detectable problems
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                //.penaltyDeath()
                .build()
        )
//        }
        MMKV.initialize(context)
        GlobalScope.launch(Dispatchers.IO) {

            startCount()
            initDataBase(context)
        }

    }

    private fun initDataBase(context: Context) {
        AppDatabase.getInstance(context).let {
            val bookDao = it.bookDao()
            val categoryDao = it.categoryDao()
            if (bookDao.count() == 0) {
                bookDao.createNewBook(
                    Book(
                        id = "0",
                        name = "个人账本",
                        createUser = "local",
                        type = "日常账本"
                    )
                )
            }
            if (categoryDao.count() == 0) {
                categoryDao.insert(incomeDefaultCategory)
                categoryDao.insert(expenditureDefaultCategory)
            }
        }
    }


    override fun dependencies(): List<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    /**
     * 统计启动次数
     */
    private fun startCount() {
        val key = "start"
        val startCount = mmkv()!!.decodeInt(key, 0)
        mmkv()!!.encode(key, startCount + 1)
    }
    fun fakeData() {
        val u1 = Dealer("锅得铁")
        val u2 = Dealer("谢大脚")
        val u3 = Dealer("赵四")
        val u4 = Dealer("刘能")
        val u5 = Dealer("永强")
        AppDatabase.getInstance().dealerDao().insert(u1)
        AppDatabase.getInstance().dealerDao().insert(u2)
        AppDatabase.getInstance().dealerDao().insert(u3)
        AppDatabase.getInstance().dealerDao().insert(u4)
        AppDatabase.getInstance().dealerDao().insert(u5)

        val startCount = mmkv()!!.decodeInt("start", 0)//首次启动
        if (startCount == 1) {
            val c0_0 = Category(ObjectId().toString(), "其他", 0, -1)
            val c0_1 = Category(ObjectId().toString(), "其他", 0, 1)
            AppDatabase.getInstance().categoryDao().insert(c0_0)
            AppDatabase.getInstance().categoryDao().insert(c0_1)
            val book = Book(name = "个人账本")
            AppDatabase.getInstance().bookDao().createNewBook(book)
            currentBook = book
        }
        if (startCount == 1) {
            val c1 = Category(ObjectId().toString(), "吃饭", 0, -1)
            val c2 = Category(ObjectId().toString(), "交通", 0, -1)
            val c3 = Category(ObjectId().toString(), "文具", 0, -1)
            val c4 = Category(ObjectId().toString(), "保险", 0, -1)
            val c5 = Category(ObjectId().toString(), "随礼", 0, -1)
            val c6 = Category(ObjectId().toString(), "日常", 0, 1)

            AppDatabase.getInstance().categoryDao().insert(c5)
            AppDatabase.getInstance().categoryDao().insert(c4)
            AppDatabase.getInstance().categoryDao().insert(c3)
            AppDatabase.getInstance().categoryDao().insert(c2)
            AppDatabase.getInstance().categoryDao().insert(c1)

            AppDatabase.getInstance().categoryDao().insert(c6)


        }
    }
}