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
class SettingViewModel : BaseViewModel<SettingAction, SettingUiState>() {
    override fun doAction(action: SettingAction) {

        when (action) {
            is SettingAction.InputWeiXInData -> {
                reading()
                launchIO({
                    inputWeiXinData(action.fileName)
                }, { readError(it.message.toString()) })
            }
            is SettingAction.InputAliPayData -> {
                reading()
                launchIO({ inputAliPayData(action.fileName) }, {
                    readError(it.message.toString())
                })
            }
        }
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

    private suspend fun inputAliPayData(fileName: String) {
        ReaderFactory.getReader(SUFFIX.CSV)?.readAliPay(fileName, result = { success, msg ->
            if (success) {
                readEnd()
            } else {
                readError(msg)
            }
        })
    }

    private suspend fun inputWeiXinData(fileName: String) {
        ReaderFactory.getReader(SUFFIX.CSV)?.readWeiXinPay(fileName, result = { success, msg ->
            if (success) {
                readEnd()
            } else {
                readError(msg)
            }
        })
    }
}