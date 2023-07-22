package com.rh.heji.data.db

import android.os.Parcelable
import androidx.room.*
import com.rh.heji.Config
import com.rh.heji.data.converters.BookUsersConverters
import com.rh.heji.data.converters.LogicConverters
import com.rh.heji.data.db.mongo.ObjectId
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

/**
 * 账本
 * @date: 2021/7/8
 * @author: 锅得铁
 *
 */

@Parcelize
@Entity(tableName = Book.TAB_NAME, indices = [Index(value = [Book.COLUMN_NAME], unique = true)])
data class Book(

    @Json(name = "_id")
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    var id: String = ObjectId().toHexString(),

    @ColumnInfo(name = COLUMN_NAME)
    var name: String,//账本名称

    @ColumnInfo(name = COLUMN_CREATE_USER)
    var createUser: String = Config.user.name,//创建人

    @ColumnInfo(name = COLUMN_TYPE)
    var type: String? = null,//账本类型

    @ColumnInfo(name = COLUMN_BANNER_URL)
    var bannerUrl: String? = null,//封面图片

    @ColumnInfo(name = COLUMN_ANCHOR)
    var anchor: Long = 0L,//锚点用作记录服务最后修改时间

    @Json(name="first_book")
    @ColumnInfo(name = COLUMN_FIRST)
    @TypeConverters(LogicConverters::class)
    var firstBook: Boolean = false

) : Parcelable {

    @ColumnInfo(name = COLUMN_SYNC_STATUS)
    var syncStatus: Int = STATUS.NOT_SYNCED

    companion object {
        const val TAB_NAME = "book"
        const val COLUMN_ID = "book_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_CREATE_USER = "create_user"
        const val COLUMN_TYPE = "type"
        const val COLUMN_BANNER_URL = "banner_url"
        const val COLUMN_ANCHOR = "anchor"
        const val COLUMN_FIRST = "first"
        const val COLUMN_SYNC_STATUS = "sync_status"
    }
}
