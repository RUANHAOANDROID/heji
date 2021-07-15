package com.rh.heji

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Book
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
    val currentUser by lazy { JWTParse.getUser(token.tokenString) }
    val heJiServer: HeJiServer by lazy {
        ServiceCreator.getInstance().createService(HeJiServer::class.java) as HeJiServer
    }
    lateinit var appViewModule: AppViewModule
    val database: AppDatabase by lazy { AppDatabase.getInstance() }
    val kvStorage: MMKV = MMKV.defaultMMKV()!!
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
        private val instance = AppCache()
        fun getInstance() = instance

        @JvmStatic
        fun init(app: Application) {
            instance.onInit(app)
        }
    }

    var currentBook = Book(name = "个人账本").apply {
        kvStorage.let { mmkv ->
            id = mmkv.decodeString(CURRENT_BOOK_ID).toString()
            name = mmkv.decodeString(CURRENT_BOOK).toString()
        }
    }
        set(value) {
            kvStorage.let { mmkv ->
                mmkv.encode(CURRENT_BOOK_ID, value.id)
                mmkv.decodeString(CURRENT_BOOK, value.name)
            }
            field = value
        }
        get() {
            kvStorage.let { mmkv ->
                field.id = mmkv.decodeString(CURRENT_BOOK_ID).toString()
                field.name = mmkv.decodeString(CURRENT_BOOK).toString()
            }
            return field
        }

}

