package com.rh.heji

import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.utlis.YearMonth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

/**
 * Date: 2020/9/16
 * Author: 锅得铁
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
const val TEST_TOKEN: String =
    "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxOTkyMTk2OTU4NiIsImF1dGgiOiJST0xFX1VTRVIsUk9MRV9BRE1JTiIsInRlbCI6IjE5OTIxOTY5NTg2IiwiZXhwIjoxNjI2ODY1NTI0fQ.GRievBu9VR-5vUrrQaVhwXMdnxfglBTnGeP-78S3Wx875XZdLAHqewa7_jzn9L2wiaElV-_X60vjTL9X2X1CbA"

val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .add(DateConverters)
    .add(MoneyConverters)
    .build()