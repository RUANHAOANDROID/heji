package com.rh.heji.utils.excel

interface IReader {
    fun readAliPay(
        fileName: String, result: (Boolean, msg: String) -> Unit
    )

    fun readWeiXinPay(
        fileName: String, result: (Boolean, msg: String) -> Unit
    )

    fun readQianJi(
        fileName: String, result: (Boolean, msg: String) -> Unit
    )
}