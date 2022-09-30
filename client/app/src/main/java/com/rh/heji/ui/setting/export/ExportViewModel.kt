package com.rh.heji.ui.setting.export

import android.os.Environment
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.App
import com.rh.heji.network.HejiNetwork
import com.rh.heji.ui.base.BaseViewModelMVI
import com.rh.heji.ui.setting.export.ExportAction.ExportExcel
import com.rh.heji.utlis.MyUtils
import com.rh.heji.utlis.launchIO
import okio.buffer
import okio.sink
import java.io.File

class ExportViewModel : BaseViewModelMVI<ExportAction, ExportUiState>() {

    override fun doAction(action: ExportAction) {
        super.doAction(action)
        when (action) {
            is ExportExcel -> {
                exportExcel(action.fileName)
            }
        }
    }

    private fun exportExcel(fileName: String) {
        launchIO({
            var response = HejiNetwork.getInstance().billExport()
            if (response.isSuccessful && response.code() == 200) {
                val filesDir =
                    App.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
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
                    uiState.postValue(ExportUiState.Success(excelFile.absolutePath))
                    LogUtils.d("下载成功：${excelFile.absolutePath}")
                    MyUtils.galleryAddPic(App.context, excelFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                    uiState.postValue(ExportUiState.Error(e))
                }
            } else {
                uiState.postValue(ExportUiState.Error(RuntimeException("导入失败 code :${response.code()} ${response.message()}")))
            }
        }, {
            uiState.postValue(ExportUiState.Error(it))
        })
    }
}
