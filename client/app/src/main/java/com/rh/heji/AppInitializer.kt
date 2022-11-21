package com.rh.heji

import android.content.Context
import android.os.StrictMode
import android.util.Log
import androidx.startup.Initializer
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.store.DataStoreManager
import com.rh.heji.ui.user.JWTParse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * startup.InitializationProvider
 * 注意： 该类在 AndroidManifest provider startup
 * @date 2022/11/13
 * @author hao
 * @see AppInitializer
 */
class AppInitializer : Initializer<Unit> {
    private lateinit var context: Context
    private val tag = "AppInitializer"
    override fun create(context: Context) {
        Log.d(tag, "create: ")
        this.context = context
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(
//                StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork() // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build()
//            )
//            StrictMode.setVmPolicy(
//                StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    //.penaltyDeath()
//                    .build()
//            )
//        }
        runBlocking {
            DataStoreManager.getUseMode(context).first()?.let {
                Config.setUseMode(it)
            }
            if (Config.enableOfflineMode) {
                Config.setUser(Config.localUser)
                Config.setBook(Config.defaultBook)
            } else {
                DataStoreManager.getToken(context).first()?.let {
                    Config.setUser(JWTParse.getUser(it))
                }
            }
            DataStoreManager.getBook(context).first()?.let {
                Config.setBook(it)
                LogUtils.d(it)
            }

            LogUtils.d(
                tag,
                "Config enableOfflineMode=${Config.enableOfflineMode}",
                "Config isInitBook=${Config.isInitBook()}",
                "Config isInitUser=${Config.isInitUser()}"
            )
        }
    }


    override fun dependencies(): List<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}