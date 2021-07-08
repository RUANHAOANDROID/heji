package com.rh.heji.data.db.query

import java.math.BigDecimal

/**
 * 收入支出在某时间
 */
data class IncomeTime(
    var income: BigDecimal?= BigDecimal.ZERO,
    var expenditure: BigDecimal?= BigDecimal.ZERO,
    var time: String?
)