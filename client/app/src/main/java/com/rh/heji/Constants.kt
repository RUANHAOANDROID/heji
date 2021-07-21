package com.rh.heji

import com.rh.heji.utlis.YearMonth
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

var currentYearMonth: YearMonth = YearMonth(
    Calendar.getInstance().get(Calendar.YEAR),
    Calendar.getInstance().get(Calendar.MONTH) + 1
)
const val TOKEN: String="Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxOTkyMTk2OTU4NiIsImF1dGgiOiJST0xFX1VTRVIsUk9MRV9BRE1JTiIsInRlbCI6IjE5OTIxOTY5NTg2IiwiZXhwIjoxNjI2ODY1NTI0fQ.GRievBu9VR-5vUrrQaVhwXMdnxfglBTnGeP-78S3Wx875XZdLAHqewa7_jzn9L2wiaElV-_X60vjTL9X2X1CbA"
