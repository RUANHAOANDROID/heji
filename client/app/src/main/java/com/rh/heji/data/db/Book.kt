package com.rh.heji.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Date: 2021/7/8
 * Author: 锅得铁
 * #
 */
@Entity(tableName = "book")
data class Book(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    var id: String,
    var name: String,
    var createUser: String? = null,
    var type: String? = null
) {
    companion object {
        const val COLUMN_ID = "id"
    }
}