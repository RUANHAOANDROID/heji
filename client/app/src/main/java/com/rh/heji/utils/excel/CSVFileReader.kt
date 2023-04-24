package com.rh.heji.utils.excel

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.opencsv.CSVReader
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
            // 创建CSVReader对象
            // val reader = CSVReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            val reader = CSVReader(InputStreamReader(inputStream, "GBK"))//支付宝导出来的是gbk格式编码

            // 读取CSV文件中的所有行并打印它们
            var nextLine: Array<String>?
            while (reader.readNext().also { nextLine = it } != null) {
                if (nextLine!!.size < 5) {
                    Log.d(Tag, Arrays.toString(nextLine))
                    continue
                }
                for (i in nextLine!!) {
                    Log.d(Tag, Arrays.toString(nextLine))
                    nextLine?.let { columns ->
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
                        Log.d(Tag, aliPay.toString())
                    }
                }
            }
            inputStream.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun readWeiXinPay(
        fileName: String, result: (Boolean) -> Unit
    ) {
    }

    override fun readQianJi(fileName: String, result: (Boolean) -> Unit) {
    }
}