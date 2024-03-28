package com.hao.heji.ui.setting.export

import android.os.Environment
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.network.HttpManager
import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.ui.setting.export.ExportAction.ExportExcel
import com.hao.heji.utils.MyUtils
import com.hao.heji.utils.launchIO
import okio.buffer
import okio.sink
import java.io.File

internal class ExportViewModel : BaseViewModel<ExportAction, ExportUiState>() {

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
            var response = HttpManager.getInstance().billExport()
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
                    send(ExportUiState.Success(excelFile.absolutePath))
                    LogUtils.d("下载成功：${excelFile.absolutePath}")
                    MyUtils.galleryAddPic(App.context, excelFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                    send(ExportUiState.Error(e))
                }
            } else {
                send(ExportUiState.Error(RuntimeException("导入失败 code :${response.code()} ${response.message()}")))
            }
        }, {
            send(ExportUiState.Error(it))
        })
    }
}
