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
@Entity(tableName = "book")
data class Book(

    @Json(name = "_id")
    @PrimaryKey()
    @NotNull
    @ColumnInfo(name = COLUMN_ID)
    var id: String = ObjectId().toHexString(),
    var name: String,
    var createUser: String? = null,
    var type: String? = null,
    var bannerUrl: String? = null,
) : Parcelable {
    @ColumnInfo(name = "sync_status")
    var synced: Int = STATUS.NOT_SYNCED

    @Json(name = "users")
    @ColumnInfo(name = "users")
    @TypeConverters(BookUsersConverters::class)
    //@Ignore
    var users: List<BookUser>? = null

    companion object {
        const val COLUMN_ID = "id"
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
