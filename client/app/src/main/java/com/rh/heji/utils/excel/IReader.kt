package com.rh.heji.utils.excel

interface IReader {
    fun readAliPay(
        fileName: String, result: (Boolean) -> Unit
    )

    fun readWeiXinPay(
        fileName: String, result: (Boolean) -> Unit
    )

    fun readQianJi(
        fileName: String, result: (Boolean) -> Unit
    )
}