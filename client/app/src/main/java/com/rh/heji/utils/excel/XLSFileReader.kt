package com.rh.heji.utils.excel

import com.blankj.utilcode.util.LogUtils
import com.rh.heji.utils.excel.entity.AliPayEntity
import jxl.Cell
import jxl.Workbook
import java.io.File


internal class XLSFileReader : IReader {
    private val Tag = "XLSFileReader"

    override fun readAliPay(fileName: String, result: (Boolean, msg: String) -> Unit) {
        val book = Workbook.getWorkbook(File(fileName))
        val sheet = book.getSheet(0)
        val rows = sheet.rows
        val columns = sheet.columns
        var call: Cell
        var startLine = 4
        for (row in startLine until rows) {
            call = sheet.getCell(0, row)
            LogUtils.d(call.contents)
            val alipay = AliPayEntity(
                sheet.getCell(1, row).contents,
                sheet.getCell(2, row).contents,
                sheet.getCell(3, row).contents,
                sheet.getCell(4, row).contents,
                sheet.getCell(5, row).contents,
                sheet.getCell(6, row).contents,
                sheet.getCell(7, row).contents,
                sheet.getCell(8, row).contents,
                sheet.getCell(9, row).contents,
                sheet.getCell(10, row).contents,
                sheet.getCell(11, row).contents,
                sheet.getCell(12, row).contents,
                sheet.getCell(13, row).contents,
                sheet.getCell(14, row).contents,
                sheet.getCell(15, row).contents,
                sheet.getCell(16, row).contents,
            )
        }
        result(true,"导入完成")
        book.close()
    }

    override fun readWeiXinPay(fileName: String, result: (Boolean, msg: String) -> Unit) {

    }

    override fun readQianJi(fileName: String, result: (Boolean, msg: String) -> Unit) {

    }
}