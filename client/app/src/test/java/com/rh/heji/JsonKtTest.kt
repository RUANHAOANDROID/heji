package com.rh.heji

import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.Bill
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert
import org.junit.Test
import java.util.*

class JsonKtTest {
    @Test
    fun jsonTest() {
        Assert.assertEquals(4, (2 + 2).toLong())
        val jsonString =
            "{\"id\":\"611e854692891153fb00ae14\",\"bookId\":\"mybook\",\"money\":0.00,\"type\":-1,\"category\":null,\"billTime\":\"2021-22-20 00:22:30\",\"updateTime\":0,\"dealer\":null,\"createUser\":\"App.getInstance().currentUser.username\",\"remark\":null,\"imgCount\":0,\"synced\":0,\"images\":[]}"

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(DateConverters)
            .add(MoneyConverters)
            .build()
        val jsonAdapter: JsonAdapter<Bill> = moshi.adapter(Bill::class.java)
        val bill: Bill = jsonAdapter.fromJson(jsonString)!!
        println(bill)
        Assert.assertEquals(Objects.isNull(bill), false)
    }
}