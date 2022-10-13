package com.rh.heji.ui.bill.create

import com.rh.heji.data.db.Category

/**
 * 选择的category
 * @date 2022/5/10
 * @author 锅得铁
 * @since v1.0
 */
interface ISelectedCategory {
    fun selected(category: Category)
}