package com.rh.heji.data.db

import androidx.room.*
import com.rh.heji.data.db.mongo.ObjectId
import java.util.*

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #收入/支出 类型标签
 */
@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    var id: String = ObjectId().toHexString(),
    @ColumnInfo(name = "category")
    var category: String,
) {
    @Ignore
    constructor(id: String=ObjectId().toHexString(), category: String, level: Int, type: Int) : this(id, category) {
        this.level = level
        this.type = type
    }

    @ColumnInfo(name = "level")
    var level: Int = 0

    /**
     * 收入、支出
     */
    @ColumnInfo(name = "type", defaultValue = "-1")
    var type: Int = 0

    /**
     * 账本ID
     */
    @JvmField
    @ColumnInfo(name = "book_id")
    var bookId: String? = null

    /**
     * 在账本下排序
     */
    @ColumnInfo(name = "index")
    var index: Int = 0

    /**
     * 父ID
     */
    @JvmField
    var parentId: String? = null

    /**
     * 用户ID
     */
    @JvmField
    @ColumnInfo(name = "user_id")
    var userId: String? = null

    /**
     * 是否在记账页面显示
     */
    @Ignore
    @ColumnInfo(name = "visibility", defaultValue = "1")
    var visibility: Int = 0

    @Ignore
    var isSelected: Boolean = false

    @ColumnInfo(name = "sync_status", defaultValue = "0")
    var synced: Int = Constant.STATUS_NOT_SYNC

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val category1 = o as Category
        return type == category1.type && category == category1.category
    }

    override fun hashCode(): Int {
        return Objects.hash(category, type)
    }

    override fun toString(): String {
        return "Category{" +
                "category='" + category + '\'' +
                ", level=" + level +
                ", type=" + type +
                ", visibility=" + visibility +
                ", selected=" + isSelected +
                ", synced=" + synced +
                '}'
    }
}