package com.rh.heji.data.db.query

import java.math.BigDecimal

/**
 * 收入|支出|结余|时间
 */
data class IncomeTimeSurplus(
    var income: BigDecimal= BigDecimal.ZERO,
    var expenditure: BigDecimal= BigDecimal.ZERO,
    var surplus: BigDecimal= BigDecimal.ZERO,
    var time: String="null"
)