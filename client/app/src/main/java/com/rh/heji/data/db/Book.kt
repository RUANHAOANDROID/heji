package com.rh.heji.data.db

import android.os.Parcelable
import androidx.room.*
import com.rh.heji.data.converters.BookUsersConverters
import com.rh.heji.data.db.mongo.ObjectId
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

/**
 * Date: 2021/7/8
 * Author: 锅得铁
 * #
 */

@Parcelize
@Entity(tableName =Book.TAB_NAME, indices = [Index(value = [Book.COLUMN_NAME], unique = true)])
data class Book(
    @Json(name = "_id")
    @PrimaryKey()
    @NotNull
    @ColumnInfo(name = COLUMN_ID)
    var id: String = ObjectId().toHexString(),
    @ColumnInfo(name = COLUMN_NAME)
    var name: String,
    @ColumnInfo(name = COLUMN_CREATE_USER)
    var createUser: String? = null,
    @ColumnInfo(name = COLUMN_TYPE)
    var type: String? = null,
    @ColumnInfo(name = COLUMN_BANNER_URL)
    var bannerUrl: String? = null,
    @ColumnInfo(name = COLUMN_FIRST)
    var firstBook: Int = 0// 0 true |1 false
) : Parcelable {

    @ColumnInfo(name = COLUMN_SYNC_STATUS)
    var synced: Int = STATUS.NOT_SYNCED

    @Json(name = "users")
    @ColumnInfo(name = COLUMN_USERS)
    @TypeConverters(BookUsersConverters::class)
    //@Ignore
    var users: List<BookUser>? = null

    companion object {
        const val TAB_NAME = "book"
        const val COLUMN_ID = "book_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_CREATE_USER = "create_user"
        const val COLUMN_TYPE = "type"
        const val COLUMN_BANNER_URL = "banner_url"
        const val COLUMN_FIRST = "first"
        const val COLUMN_SYNC_STATUS = "sync_status"
        const val COLUMN_USERS = "users"
    }
}

@Parcelize
class BookUser(val name: String, val authority: String) : Parcelable {
    @Ignore
    fun fromAuthority(): String {
        if (authority == "CREATE") return "创建者"
        if (authority == "USER") return "用户"
        return authority
    }
}
