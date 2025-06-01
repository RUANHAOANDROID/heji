package com.hao.heji

import com.hao.heji.data.converters.DateConverters
import com.hao.heji.data.converters.MoneyConverters
import com.hao.heji.utils.YearMonth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

val currentYearMonth: YearMonth = today()

fun today(): YearMonth = YearMonth(
    year = Calendar.getInstance().get(Calendar.YEAR),
    month = Calendar.getInstance().get(Calendar.MONTH) + 1,
    day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
)

val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .add(DateConverters)
    .add(MoneyConverters)
    .build()
