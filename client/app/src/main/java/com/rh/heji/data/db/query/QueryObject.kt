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

/**
 * 收入|支出|结余|时间
 */
data class IncomeTimeSurplus(
        var income: BigDecimal?,
        var expenditure: BigDecimal?,
        var surplus: BigDecimal?,
        var time: String?
)

/**
 * 分类所占比例
 * 收入|支出|占比
 */
data class CategoryPercentage(var category: String?,
                              var money: BigDecimal?,
                              var percentage: Float)

/**
 * 平均收入支出
 */
data class AvgIE(var income: BigDecimal?, var expenditure: BigDecimal?)

