package com.rh.heji.utils.excel

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.opencsv.CSVReader
import com.opencsv.bean.ConverterDate
import com.rh.heji.App
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.utils.excel.entity.AliPayEntity
import com.rh.heji.utils.excel.entity.WeiXinPayEntity
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal


internal class CSVFileReader : IReader {
    private val TAG = "CSVFileReader"
    override fun readAliPay(fileName: String, result: (Boolean, msg: String) -> Unit) {
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
                    Log.d(TAG, columns.contentToString())
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
                        id =
                            ObjectId(DateConverters.str2Date(aliPay.lastModifiedTime)).toHexString()
                        money = aliPay.money.toBigDecimal()
                        type = aliPayType
                        time = TimeUtils.string2Date(aliPay.lastModifiedTime, "yyyy-MM-dd HH:mm:ss")
                        category = "支付宝" //aliPay.counterparty
                        remark = "${aliPay.counterparty}${aliPay.remark}"
                    }
                    if (bill.money.compareTo(BigDecimal.ZERO) == 0) {
                        // 等于0则不计入
                        continue
                    }
                    App.dataBase.billDao().let {
                        Log.d(TAG, "install : $bill")
                        //判断是否已经存在
                        val exist = it.exist(bill.hashCode()) > 0
                        Log.d(TAG, "install exist : $exist hash = ${bill.hashCode()}")
                        if (!exist) {
                            it.install(bill)//记账
                            Log.d(TAG, "install : OK")
                        }
                    }
                    //记账
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d(TAG, aliPay.toString())
            }
            result(true, "导入完成")
            inputStream.close()
            inputStreamReader.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
            result(false, "${e.message}")
        }
    }

    override fun readWeiXinPay(
        fileName: String, result: (Boolean, msg: String) -> Unit
    ) {
        try {
            // 从应用程序资源中读取CSV文件
            val inputStream = FileInputStream(fileName)
            //支付宝导出来的是gbk格式编码
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            // 创建CSVReader对象
            val reader = CSVReader(inputStreamReader)
            // 读取CSV文件中的所有行并打印它们
            var nextLine: Array<String>?
            while (reader.readNext().also { nextLine = it } != null) {
                //交易时间|交易类型|交易对方|商品|收/支|金额(元)|支付方式|当前状态|交易单号|商户单号|备注

                val columns = nextLine!!
                //正常账单会大于5列，标题除外
                if (columns.size < 9) {
                    Log.d(TAG, columns.contentToString())
                    continue
                }
                if (columns[4] != "支出" && columns[4] != "支付") continue
                val weiPay = WeiXinPayEntity(
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
                    //columns[10].trim(),
                )
                val weiMoney = weiPay.money.split("¥")[1]

                //仅仅计入收入和支出
                var aliPayType: Int = if (weiPay.receiptOrExpenditure == "支出") {
                    BillType.EXPENDITURE.valueInt
                } else if (weiPay.receiptOrExpenditure == "收入") {
                    BillType.INCOME.valueInt
                } else {
                    continue
                }
                try {
                    //转换
                    val bill = Bill().apply {
                        id = ObjectId(DateConverters.str2Date(weiPay.transactionTime)).toHexString()
                        money = weiMoney.toBigDecimal()
                        type = aliPayType
                        time = TimeUtils.string2Date(weiPay.transactionTime, "yyyy-MM-dd HH:mm:ss")
                        category = "微信"
                        remark = "${weiPay.counterparty}${weiPay.remark}"
                    }
                    if (bill.money.compareTo(BigDecimal.ZERO) == 0) {
                        // 等于0则不计入
                        continue
                    }
                    App.dataBase.billDao().let {
                        //判断是否已经存在
                        val exist = it.exist(bill.hashCode()) > 0
                        if (!exist) {
                            LogUtils.d(bill)
                            it.install(bill)//记账
                        }
                    }
                    //记账
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d(TAG, weiPay.toString())
            }
            result(true, "导入完成")
            inputStream.close()
            inputStreamReader.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
            result(false, "${e.message}")
        }
    }

    override fun readQianJi(fileName: String, result: (Boolean, msg: String) -> Unit) {
    }
}