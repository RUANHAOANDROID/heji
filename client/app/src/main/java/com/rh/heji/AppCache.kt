package com.rh.heji

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rh.heji.data.AppDatabase
import com.rh.heji.network.HeJiServer
import com.rh.heji.security.Token
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.http.basic.ServiceCreator
import com.tencent.mmkv.MMKV
import java.io.*

/**
 * Date: 2020/11/18
 * Author: 锅得铁
 * #与APP同在
 */
class AppCache {
    val app: App by lazy { context as App }
    lateinit var context: Context
    val token by lazy { Token(context) }
    val user by lazy { JWTParse.getUser(token.tokenString) }
    val heJiServer: HeJiServer by lazy {
        ServiceCreator.getInstance().createService(HeJiServer::class.java) as HeJiServer
    }
    lateinit var appViewModule: AppViewModule
    val database: AppDatabase by lazy { AppDatabase.getInstance() }
    val kvStorage = MMKV.defaultMMKV()
    fun onInit(app: Application) {
        context = app
        appViewModule = AppViewModule(app)
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


    companion object {
        val instance = AppCache()

        @JvmStatic
        fun init(app: Application) {
            instance.onInit(app)
        }
    }


}

