package com.rh.heji.network.request

import com.rh.heji.data.db.Category

/**
 * Date: 2020/9/24
 * @author: 锅得铁
 * #标签
 */

data class CategoryEntity(val category: Category) {
    var id: String = category.id
    var bookId: String = category.bookId
    var name: String = category.name
    var type: Int = category.type
    var level: Int = category.level
    fun toDbCategory(): Category {
        return Category(bookId, name).apply {
            id = category.id
            level = category.level
            type = category.type
        }
    }

}