package com.rh.heji.data.db.query

import java.math.BigDecimal

data class Income(
        var income: BigDecimal?,
        var expenditure: BigDecimal?,
        var time: String?
) {
}