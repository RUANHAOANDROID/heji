package com.rh.heji.data.db

import android.os.Parcelable
import androidx.room.*
import com.rh.heji.App
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.mongo.ObjectId
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

/**
 * Date: 2020/8/28
 * @author: 锅得铁
 * # onDelete ：当账本创建人删除了账本，联动删除账本账单
 * # onUpdate ：当账本更新，账单不联动
 */
@Parcelize
@Entity(
    tableName = Bill.TAB_NAME, primaryKeys = [Bill.COLUMN_ID], foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = [Book.COLUMN_ID],
        childColumns = [Bill.COLUMN_BOOK_ID],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION
    )], indices = [Index(value = [Bill.COLUMN_ID, Bill.COLUMN_BOOK_ID], unique = true)]
)
@TypeConverters(DateConverters::class, MoneyConverters::class)
data class Bill(
    @Json(name = "_id")
    @ColumnInfo(name = COLUMN_ID)
    var id: String = ObjectId().toHexString(),

    @ColumnInfo(name = COLUMN_BOOK_ID, index = true)
    var bookId: String = App.currentBook.id,
    /**
     * 钱
     */
    @TypeConverters(MoneyConverters::class)
    var money: BigDecimal = MoneyConverters.ZERO_00(),

    /**
     * 收支类型 s|z
     */
    var type: Int = BillType.EXPENDITURE.valueInt(),

    /**
     * 类别
     */

    var category: String? = null,

    /**
     * 账单时间-产生费用的日期-以这个为主
     */

    @ColumnInfo(name = "bill_time")
    var billTime: Date = Date(),

    /**
     * 创建时间
     */
    @ColumnInfo(name = "create_time")
    var createTime: Long? = 0,

    /**
     * 更新时间
     */
    @ColumnInfo(name = "update_time")
    var updateTime: Long? = 0,

    /**
     * 用户标签，费用产生人
     */
    var dealer: String? = null,

    @ColumnInfo(name = "create_user")
    var createUser: String = App.user.name,

    /**
     * 备注
     */
    var remark: String? = null,

    @Ignore
    @ColumnInfo(name = "images")
    var images: List<String> = mutableListOf(),

    @ColumnInfo(name = "sync_status")
    var synced: Int = STATUS.NOT_SYNCED,

    @ColumnInfo(name = "anchor")
    var anchor: Long = 0L,//锚点用作记录服务最后修改时间

) : Parcelable {

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
                ", synced=" + synced +
                '}'
    }

    companion object {
        const val TAB_NAME = "bill"
        const val COLUMN_ID = "bill_id"
        const val COLUMN_BOOK_ID = Book.COLUMN_ID
    }
}