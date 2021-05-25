package com.rh.heji

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelStore
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.utlis.http.basic.HttpRetrofit
import com.rh.heji.utlis.http.basic.OkHttpConfig.clientBuilder
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    //.penaltyDeath()
                    .build())
        }

        HttpRetrofit.initClient(clientBuilder.build())
        AppCache.init(this)
        AppCache.instance.appViewModule

        startCount()
    }

    private fun startCount() {
        val key = "start"
        val startCount = AppCache.instance.kvStorage!!.decodeInt(key, 0)
        LogUtils.d(startCount)
        AppCache.instance.kvStorage.encode(key, startCount + 1)
    }

    override fun onTerminate() {
        if (BuildConfig.DEBUG) {
        }
        super.onTerminate()
    }

}