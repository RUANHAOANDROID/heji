package com.rh.heji.data.db.query

import java.math.BigDecimal

//注意:数据类没办法集成open 因为没法实现equals
/**
 * 收入支出
 */
data class Income(
        var income: BigDecimal?,
        var expenditure: BigDecimal?
)

/**
 * 收入支出在某时间
 */
data class IncomeTime(
        var income: BigDecimal?,
        var expenditure: BigDecimal?,
        var time: String?
)