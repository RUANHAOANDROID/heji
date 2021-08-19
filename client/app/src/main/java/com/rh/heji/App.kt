package com.rh.heji

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
import com.rh.heji.security.Token
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.http.basic.HttpRetrofit
import com.rh.heji.utlis.http.basic.OkHttpConfig
import com.tencent.mmkv.MMKV
import java.io.File

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
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
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    //.penaltyDeath()
                    .build()
            )
        }
        startup(this)
        instance.init(this)
    }

    private fun startup(app: App) {
        startCount()
    }

    private fun startCount() {
        val key = "start"
        val startCount = instance.mmkv!!.decodeInt(key, 0)
        LogUtils.d(startCount)
        instance.mmkv?.encode(key, startCount + 1)
    }

    override fun onTerminate() {
        if (BuildConfig.DEBUG) {
        }
        super.onTerminate()
    }

    companion object {
        @JvmName("getInstance1")
        fun instance() = instance

        @JvmName("getInstance2")
        fun getInstance() = instance

        val instance by lazy { Constants() }

        class Constants internal constructor() {
            lateinit var context: Context
            lateinit var database: AppDatabase
            lateinit var token: Token
            lateinit var currentUser: JWTParse.User
            lateinit var appViewModule: AppViewModule

            val mmkv: MMKV = MMKV.defaultMMKV()!!
            fun init(context: Context) {
                this.context = context
                database = AppDatabase.getInstance(context)
                token = Token(context)
                currentUser = JWTParse.getUser(token.readTokenFile())
                HttpRetrofit.initClient(OkHttpConfig.clientBuilder.build())
                appViewModule = AppViewModule(context as Application)
            }

            /**
             * 当前账本
             */
            var currentBook = Book(
                id = mmkv.decodeString(CURRENT_BOOK_ID).toString(),
                name = mmkv.decodeString(CURRENT_BOOK).toString()
            )
                set(value) {
                    mmkv.let { mmkv ->
                        mmkv.encode(CURRENT_BOOK_ID, value.id)
                        mmkv.encode(CURRENT_BOOK, value.name)
                    }
                    field = value
                }

            /**
             * 把文件公开。添加到扫描中
             *
             * @param currentPhotoPath
             */
            fun galleryAddPic(currentPhotoPath: String) {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val f = File(currentPhotoPath)
                val contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)
            }

            fun storage(path: String?): String {
                val headDir = context.getExternalFilesDir(path)
                if (!headDir!!.exists()) headDir.mkdir()
                return headDir.path
            }
        }


    }


}
