package com.rh.heji.ui.setting.export

import android.os.Environment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.haibin.calendarview.BaseView
import com.rh.heji.App
import com.rh.heji.AppCache
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import java.io.File


class ExportViewModel : BaseViewModel() {
    var exportLiveData = MediatorLiveData<String>()

    init {


    }

    fun exportExcel(fileName: String): MediatorLiveData<String> {
        launchIO({
            var response = AppCache.getInstance().heJiServer.exportBills("0", "0").execute()
            if (response.isSuccessful && response.code() == 200) {
                val filesDir =
                    AppCache.getInstance().context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                var attachment = response.headers()["Content-Disposition"]
                var subFileName = attachment?.substringAfterLast(
                    "attachment; filename=",
                    System.currentTimeMillis().toString() + ".xlsx"
                )
                val excelFile = File(filesDir, subFileName)
                try {
                    val sink = excelFile.sink().buffer()
                    response.body()?.source()?.let { sink.writeAll(it) }
                    sink.flush()
                    sink.close()
                    exportLiveData.postValue(excelFile.absolutePath)
                    LogUtils.d("下载成功：${excelFile.absolutePath}")
                    AppCache.getInstance().galleryAddPic(excelFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                exportLiveData.postValue(response.message())
            }
        }, {})

        return exportLiveData;
    }
}
