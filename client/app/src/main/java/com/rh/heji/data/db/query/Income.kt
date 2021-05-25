package com.rh.heji.data.db.query

import java.math.BigDecimal

/**
 * 收入支出
 */
data class Income(
    var income: BigDecimal = BigDecimal.ZERO,
    var expenditure: BigDecimal = BigDecimal.ZERO
)