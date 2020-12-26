package com.rh.heji.ui.export

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.App
import com.rh.heji.AppCache
import kotlinx.coroutines.Dispatchers
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.db.BillWithImage
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import java.io.File


class ExportViewModel : ViewModel() {
    var exportLiveData = MediatorLiveData<String>()

    init {


    }

    fun exportExcel(fileName: String): MediatorLiveData<String> {
        viewModelScope.launch(Dispatchers.IO) {
            var response = AppCache.getInstance().heJiServer.exportBills("0", "0").execute()
            if (response.isSuccessful && response.code() == 200) {
                val filesDir = App.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                var attachment = response.headers()["Content-Disposition"]
                var fileName = attachment?.substringAfterLast("attachment; filename=", System.currentTimeMillis().toString() + ".xlsx")
                val excelFile = File(filesDir, fileName)
                try {
                    val sink = excelFile.sink().buffer()
                    response.body()?.source()?.let { sink.writeAll(it) }
                    sink.flush()
                    sink.close()
                    exportLiveData.postValue(excelFile.absolutePath)
                    LogUtils.i("下载成功：${excelFile.absolutePath}")
                    AppCache.getInstance().galleryAddPic(excelFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                exportLiveData.postValue(response.message())
            }
        }
        return exportLiveData;
    }
}
