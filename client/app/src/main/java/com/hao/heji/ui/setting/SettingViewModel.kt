package com.hao.heji.ui.setting

import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.utils.excel.ReaderFactory
import com.hao.heji.utils.excel.SUFFIX
import com.hao.heji.utils.launchIO

/**
 *
 * @date 2023/4/25
 * @author 锅得铁
 * @since v1.0
 */
class SettingViewModel : BaseViewModel<SettingUiState>() {
    fun inputAlipayData(fileName: String) {
        reading()
        launchIO({
            ReaderFactory.getReader(SUFFIX.CSV)?.readAliPay(fileName, result = { success, msg ->
                if (success) {
                    readEnd()
                } else {
                    readError(msg)
                }
            })
        }, {
            readError(it.message.toString())
        })
    }

    fun inputWeixinData(fileName: String) {
        reading()
        launchIO({

            ReaderFactory.getReader(SUFFIX.CSV)?.readWeiXinPay(fileName, result = { success, msg ->
                if (success) {
                    readEnd()
                } else {
                    readError(msg)
                }
            })
        }, { readError(it.message.toString()) })
    }

    private fun reading() {
        send(SettingUiState.InputReading("正在导入.."))
    }

    private fun readError(it: String) {
        send(SettingUiState.InputError("导入失败:${it}"))
    }

    private fun readEnd() {
        send(SettingUiState.InputEnd("导入完成"))
    }

}