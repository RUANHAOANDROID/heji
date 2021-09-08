package com.rh.heji

import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Dealer
import com.rh.heji.data.db.mongo.ObjectId
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

}