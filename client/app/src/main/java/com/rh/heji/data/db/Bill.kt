package com.rh.heji.data.db

import androidx.room.*
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.mongo.ObjectId
import java.math.BigDecimal
import java.util.*

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@Entity(tableName = "bill")
data class Bill(
    @PrimaryKey
    @ColumnInfo(name = "bill_id")
    var id: String = ObjectId().toHexString(),

    /**
     * 钱
     */
    @TypeConverters(MoneyConverters::class)
    @ColumnInfo(name = "money")
    var money: BigDecimal = BigDecimal.ZERO,

    /**
     * 收支类型 s|z
     */
    @ColumnInfo(name = "type")
    var type: Int = 0,

    /**
     * 类别
     */
    @ColumnInfo(name = "category")
    var category: String? = null,

    /**
     * 账单时间-产生费用的日期-以这个为主
     */
    @TypeConverters(DateConverters::class)
    @ColumnInfo(name = "bill_time")
    var billTime: Date? = null,

    /**
     * 创建时间
     */
    @ColumnInfo(name = "create_time")
    var createTime //记账时间
    : Long = 0,

    /**
     * 更新时间
     */
    @ColumnInfo(name = "update_time")
    var updateTime //记账时间
    : Long = 0,

    /**
     * 用户标签，费用产生人
     */
    @ColumnInfo(name = "dealer")
    var dealer: String? = null,

    @ColumnInfo(name = "create_user")
    var createUser: String? = null,

    /**
     * 备注
     */
    @ColumnInfo(name = "remark")
    var remark: String? = null,

    @ColumnInfo(name = "img_count")
    var imgCount: Int = 0,

    @ColumnInfo(name = "sync_status")
    var synced: Int = Constant.STATUS_NOT_SYNC

) {

    @Ignore
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val bill = o as Bill
        return id == bill.id
    }

    @Ignore
    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    @Ignore
    override fun toString(): String {
        return "Bill{" +
                "id='" + id + '\'' +
                ", money=" + money +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", time=" + billTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", dealer='" + dealer + '\'' +
                ", remark='" + remark + '\'' +
                ", imgCount=" + imgCount +
                ", synced=" + synced +
                '}'
    }

    companion object {
        const val COLUMN_ID = "bill_id"
    }
}