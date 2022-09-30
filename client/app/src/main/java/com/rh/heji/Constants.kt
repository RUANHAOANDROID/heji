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

/**
 * 默认账本
 */
//var currentBook = Book(
//    name = "个人账本",
//    createUser = "local",
//    type = "日常账本"
//)

val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .add(DateConverters)
    .add(MoneyConverters)
    .build()

object ETC {
    const val URL =
        "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcsearch"
    const val ID = "42021909230571219224"
    const val CAR_ID = "鄂FNA518"

    //伪装User-Agent
    val USER_AGENTS =
        arrayOf( //"Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0",
            "Mozilla/5.0 (Linux; Android 10; MI 8 Lite Build/QKQ1.190910.002; wv) ",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36"
        )
}