package com.rh.heji.data.db

import android.os.Parcelable
import androidx.room.*
import com.rh.heji.AppViewModule
import com.rh.heji.currentBook
//import com.rh.heji.App
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.mongo.ObjectId
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@Parcelize
@Entity(
    tableName = "bill", foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = [Book.COLUMN_ID],
        childColumns = [Bill.COLUMN_BOOK_ID],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )], indices = [Index(value = ["id", "book_id"], unique = true)]
)
data class Bill(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    var id: String = ObjectId().toHexString(),

    @ColumnInfo(name = COLUMN_BOOK_ID)
    var bookId: String = currentBook.id,
    /**
     * 钱
     */
    @TypeConverters(MoneyConverters::class)
    var money: BigDecimal = BigDecimal.ZERO,

    /**
     * 收支类型 s|z
     */
    var type: Int = BillType.EXPENDITURE.type(),

    /**
     * 类别
     */

    var category: String? = null,

    /**
     * 账单时间-产生费用的日期-以这个为主
     */
    @TypeConverters(DateConverters::class)
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
    var createUser: String = "App.getInstance().currentUser.username",

    /**
     * 备注
     */
    var remark: String? = null,

    @ColumnInfo(name = "img_count")
    var imgCount: Int = 0,

    @ColumnInfo(name = "sync_status")
    var synced: Int = STATUS.NOT_SYNCED

) : Parcelable {

    @Ignore
    var images: Array<String> = arrayOf()

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
        const val COLUMN_ID = "id"
        const val COLUMN_BOOK_ID = "book_id"
    }
}