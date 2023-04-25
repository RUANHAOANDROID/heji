package com.rh.heji.utils.excel

import android.util.Log
import com.blankj.utilcode.util.TimeUtils
import com.opencsv.CSVReader
import com.rh.heji.App
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


internal class CSVFileReader : IReader {
    private val Tag = "CSVFileReader"
    override fun readAliPay(fileName: String, result: (Boolean) -> Unit) {
        try {
            // 从应用程序资源中读取CSV文件
            val inputStream = FileInputStream(fileName)
            //支付宝导出来的是gbk格式编码
            val inputStreamReader = InputStreamReader(inputStream, "GBK")
            // 创建CSVReader对象
            val reader = CSVReader(inputStreamReader)
            // 读取CSV文件中的所有行并打印它们
            var nextLine: Array<String>?
            while (reader.readNext().also { nextLine = it } != null) {
                val columns = nextLine!!
                //正常账单会大于5列，标题除外
                if (columns.size < 5) {
                    Log.d(Tag, columns.contentToString())
                    continue
                }
                val aliPay = AliPayEntity(
                    columns[0].trim(),
                    columns[1].trim(),
                    columns[2].trim(),
                    columns[3].trim(),
                    columns[4].trim(),
                    columns[5].trim(),
                    columns[6].trim(),
                    columns[7].trim(),
                    columns[8].trim(),
                    columns[9].trim(),
                    columns[10].trim(),
                    columns[11].trim(),
                    columns[12].trim(),
                    columns[13].trim(),
                    columns[14].trim(),
                    columns[15].trim(),
                )
                //仅仅计入收入和支出
                var aliPayType: Int = if (aliPay.receiptOrExpenditure == "支出") {
                    BillType.EXPENDITURE.valueInt
                } else if (aliPay.receiptOrExpenditure == "收入") {
                    BillType.INCOME.valueInt
                } else {
                    continue
                }
                try {
                    //转换
                    val bill = Bill().apply {
                        money = aliPay.amount.toBigDecimal()
                        type = aliPayType
                        time = TimeUtils.string2Date(aliPay.lastModifiedTime, "yyyy-MM-dd HH:mm:ss")
                        category = aliPay.counterparty
                        remark = "支付宝|${aliPay.remark}${aliPay.counterparty}"
                    }
                    App.dataBase.billDao().let {
                        //判断是否已经存在
                        val exist = it.exist(bill.hashValue) > 0
                        if (!exist)
                            it.install(bill)//记账
                    }
                    //记账
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d(Tag, aliPay.toString())
            }
            result(true)
            inputStream.close()
            inputStreamReader.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
            result(false)
        }
    }

    override fun readWeiXinPay(
        fileName: String, result: (Boolean) -> Unit
    ) {
    }

    override fun readQianJi(fileName: String, result: (Boolean) -> Unit) {
    }
}