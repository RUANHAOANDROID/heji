package com.rh.heji.network.request

import android.provider.ContactsContract
import com.rh.heji.data.db.Category

/**
 * Date: 2020/9/24
 * Author: 锅得铁
 * #标签
 */

data class CategoryEntity(val category: Category) {
    var id: String = category.id
    var name: String = category.category
    var type: Int = category.type
    var level: Int = category.level
    fun toDbCategory(): Category {
        return Category(id, name, level, type)
    }

}