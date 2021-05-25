package com.rh.heji.data.db.query

import java.math.BigDecimal

/**
 * 分类所占比例
 * 收入|支出|占比
 */
data class CategoryPercentage(
    var category: String?,
    var money: BigDecimal?,
    var percentage: Float
) {
    constructor() : this(category = null, money = BigDecimal.ZERO, percentage = 0.00f)
}