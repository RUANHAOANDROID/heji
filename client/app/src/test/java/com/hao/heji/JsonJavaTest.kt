package com.hao.heji

import com.hao.heji.data.converters.DateConverters.date2Str
import com.hao.heji.data.converters.DateConverters.str2Date
import com.hao.heji.data.converters.MoneyConverters.toString
import com.hao.heji.data.db.Bill
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test
import java.io.IOException
import java.math.BigDecimal
import java.util.Date

class JsonJavaTest {
    @Test
    @Throws(IOException::class)
    fun jsonTest() {
        val jsonString =
            "{\"id\":\"611e854692891153fb00ae14\",\"bookId\":\"mybook\",\"money\":0.00,\"type\":-1,\"category\":null,\"time\":\"2021-22-20 00:22:30\",\"updateTime\":0,\"dealer\":null,\"createUser\":\"App.getInstance().currentUser.username\",\"remark\":null,\"imgCount\":0,\"syncStatus\":0,\"images\":[]}"
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory()) //Java 测试添加kotlin转换器
            .add(DateAdapter())
            .add(MoneyAdapter())
            .build()
        val jsonAdapter = moshi.adapter<Bill?>(Bill::class.java)
        val billEntity = jsonAdapter.fromJson(jsonString)
        println(billEntity)
    }
}

internal class DateAdapter {
    @ToJson
    fun toJson(date: Date?): String {
        return date2Str(date)
    }


    @FromJson
    fun fromJson(date: String?): Date {
        return str2Date(date)
    }
}

internal class MoneyAdapter {
    @ToJson
    fun toJson(money: BigDecimal): String {
        return toString(money)
    }

    @FromJson
    fun fromJson(money: String?): BigDecimal {
        return BigDecimal(money)
    }
}

internal class Entity {
    var userName: String? = null
    var age: Int = 0
}