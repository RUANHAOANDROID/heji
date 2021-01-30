package com.rh.heji.network.request

import android.provider.ContactsContract
import com.rh.heji.data.db.Category

/**
 * Date: 2020/9/24
 * Author: 锅得铁
 * #标签
 */

data class CategoryEntity(val category: Category) {
    var _id: String? = null
    var name: String
    var type: Int
    var level: Int
    fun toDbCategory(): Category? {
        return _id?.let { Category(it, name, level, type) }
    }

    init {
        name = category.category
        type = category.type
        level = category.level
    }
}