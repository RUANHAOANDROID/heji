package com.rh.heji

import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * startup.InitializationProvider
 * 注意： 该类在 AndroidManifest provider startup
 */
class AppInitializer : Initializer<Unit> {
    private lateinit var context: Context
    override fun create(context: Context) {
        this.context = context
        GlobalScope.launch(Dispatchers.IO) {
            DataStoreManager.startupCount(context)
            LogUtils.getConfig().apply {
                isLogSwitch = BuildConfig.DEBUG
                stackDeep = 1
            }
        }
        if (BuildConfig.DEBUG) {
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
        }


    }


    override fun dependencies(): List<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}