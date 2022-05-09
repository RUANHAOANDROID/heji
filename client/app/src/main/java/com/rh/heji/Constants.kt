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
import com.tencent.mmkv.MMKV
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

/**
 * 当前账本
 */
const val CURRENT_BOOK = "currentBook"

/**
 * 当前账本ID
 */
const val CURRENT_BOOK_ID = "currentBookID"

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

val incomeDefaultCategory = Category(category = "其他", bookId = "", level = 0, type = BillType.INCOME.type())
val expenditureDefaultCategory =
    Category(category = "其他", bookId = "", level = 0, type = BillType.EXPENDITURE.type())

/**
 * 当前账本
 */
var currentBook = Book(
    id = MMKV.defaultMMKV()!!.decodeString(CURRENT_BOOK_ID,ObjectId.get().toHexString()).toString(),
    name = MMKV.defaultMMKV()!!.decodeString(CURRENT_BOOK,"个人账本").toString(),
    createUser = currentUser.username,
    type = "日常账本"
)
    set(value) {
        MMKV.defaultMMKV().let { mmkv ->
            mmkv!!.encode(CURRENT_BOOK_ID, value.id)
            mmkv.encode(CURRENT_BOOK, value.name)
        }
        field = value
    }

val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .add(DateConverters)
    .add(MoneyConverters)
    .build()