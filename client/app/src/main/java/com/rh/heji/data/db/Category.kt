package com.rh.heji.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.rh.heji.data.db.mongo.ObjectId
import java.util.*

/**
 * Date: 2020/8/28
 * @author: 锅得铁
 * #收入/支出 类型标签
 */
@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    var id: String = ObjectId().toHexString(),
    /**
     * 账本ID
     */
    @JvmField
    @ColumnInfo(name = "book_id")
    val bookId: String,

    @ColumnInfo(name = "name")
    var name: String = "其他",
    /**
     * 收入、支出
     */
    @ColumnInfo(name = "type", defaultValue = "-1")
    var type: Int = 0
) {


    @ColumnInfo(name = "level")
    var level: Int = 0


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
     * 同步状态
     */
    @ColumnInfo(name = "sync_status", defaultValue = "0")
    var synced: Int = STATUS.NOT_SYNCED

    /**
     * 是否在记账页面显示
     */
    @Ignore
    @ColumnInfo(name = "visibility", defaultValue = "1")
    var visibility: Int = 0

    @Ignore
    var isSelected: Boolean = false


    override fun hashCode(): Int {
        return Objects.hash(name, type)
    }
}