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
import com.rh.heji.utlis.launchIO
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
        instance.init(this)
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
            val mmkv: MMKV = MMKV.defaultMMKV()!!
            lateinit var appViewModule: AppViewModule

            //------------Async Init----------
            var jwtString: String? = null
            var currentUser: JWTParse.User = JWTParse.User("local", mutableListOf(), "")

            fun init(context: Context) {
                this.context = context
                token = Token(context)
                database = AppDatabase.getInstance(context)
                HttpRetrofit.initClient(OkHttpConfig.clientBuilder.build())
                appViewModule = AppViewModule(context as Application)
                appViewModule.run {
                    launchIO({
                        jwtString = token.decodeToken()
                        jwtString?.let {
                            currentUser = JWTParse.getUser(it)
                        }
                    })
                }
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
