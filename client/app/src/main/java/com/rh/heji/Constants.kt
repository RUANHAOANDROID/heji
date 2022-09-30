package com.rh.heji

import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.utlis.YearMonth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

/**
 * Date: 2020/9/16
 * @author: 锅得铁
 * #
 */
/**
 * 透明度
 */
const val BACKGROUND_ALPHA = 255 //0-255

/**
 * 小数点
 */
const val KEY_POINT = "."

/**
 * 压缩阀值大小 Luban 单位为K
 */
const val COMPRESSION_SIZE = 1204 * 2

/**
 * 1M 文件大小以Bytes为单位
 */
const val FILE_LENGTH_1M = 1204 * 1024 * 1

val currentYearMonth: YearMonth = YearMonth(
    year = Calendar.getInstance().get(Calendar.YEAR),
    month = Calendar.getInstance().get(Calendar.MONTH) + 1,
    day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
)
//var currentBook = Book(
//    name = "个人账本",
//    createUser = "local",
//    type = "日常账本"
//)
var currentUser: JWTParse.User = JWTParse.User("local", mutableListOf(), "")



val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .add(DateConverters)
    .add(MoneyConverters)
    .build()