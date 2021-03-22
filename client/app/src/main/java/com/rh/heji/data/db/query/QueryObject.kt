package com.rh.heji.data.db.query

import java.math.BigDecimal

//注意:数据类没办法集成open 因为没法实现equals
data class Income(
        var income: BigDecimal?,
        var expenditure: BigDecimal?,
        var time: String?
) {
}