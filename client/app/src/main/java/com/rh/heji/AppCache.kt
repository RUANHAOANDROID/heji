package com.rh.heji

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.FileUtils
import com.google.gson.GsonBuilder
import com.rh.heji.network.HeJiServer
import com.rh.heji.utlis.http.basic.ServiceCreator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.*
import java.nio.charset.StandardCharsets
import kotlin.coroutines.CoroutineContext

/**
 * Date: 2020/11/18
 * Author: 锅得铁
 * #与APP同在
 */
class AppCache {
    internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
        override val coroutineContext: CoroutineContext = context

        override fun close() {
            coroutineContext.cancel()
        }
    }


    var context: Context? = null
    lateinit var heJiServer: HeJiServer
    val gson = GsonBuilder().create()
    lateinit var appViewModule: AppViewModule

    fun onInit(app: Application) {
        context = app
        heJiServer = ServiceCreator.getInstance().createService(HeJiServer::class.java) as HeJiServer
        appViewModule = AppViewModule(app)
    }


    /**
     * 把文件公开。添加到扫描中
     *
     * @param currentPhotoPath
     */
    fun galleryAddPic(currentPhotoPath: String?) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(currentPhotoPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context!!.sendBroadcast(mediaScanIntent)
    }

    // Error occurred when opening raw file for reading.
    val token: String
        get() {
            val tokenFile = File(context!!.filesDir, "TokenFile")
            var token = ""
            if (tokenFile.exists()) {
                try {
                    val inputStream = FileInputStream(tokenFile)
                    val inputStreamReader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
                    val stringBuilder = StringBuilder()
                    try {
                        BufferedReader(inputStreamReader).use { reader ->
                            var line = reader.readLine()
                            while (line != null) {
                                stringBuilder.append(line)
                                line = reader.readLine()
                            }
                        }
                    } catch (e: IOException) {
                        // Error occurred when opening raw file for reading.
                        e.printStackTrace()
                    } finally {
                        token = stringBuilder.toString()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
            return String(EncodeUtils.base64Decode(token))
        }

    fun saveToken(token: String?) {
        //token = EncodeUtils.base64Encode(token);
        val fileName = "TokenFile"
        val file = File(context!!.filesDir.absolutePath)
        val tokenFile = File(file, fileName)
        FileUtils.createFileByDeleteOldFile(tokenFile)
        try {
            context!!.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos -> fos.write(EncodeUtils.base64Encode(token)) }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun deleteToken() {
        val fileName = "TokenFile"
        val file = File(context!!.filesDir.absolutePath)
        val tokenFile = File(file, fileName)
        FileUtils.delete(tokenFile)
    }

    override fun hashCode(): Int {
        return appViewModule?.hashCode() ?: 0
    }

    val isLogin: Boolean
        get() = !TextUtils.isEmpty(token)

    companion object {
        val instance = AppCache()

        @JvmStatic
        fun init(app: Application) {
            instance.onInit(app)
        }
    }
}