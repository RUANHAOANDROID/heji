package com.rh.heji.data.db

import android.os.Parcelable
import androidx.room.*
import com.rh.heji.data.db.mongo.ObjectId
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Parcelize
@Entity(
    tableName = "book_user", foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = [Book.COLUMN_ID],
        childColumns = [BookUser.COLUMN_FOREIGN_ID],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class BookUser(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = COLUMN_ID)
    @Json(name = "name")
    val name: String,//主键也是用户名

    @Transient//moshi 忽略字段
    @ColumnInfo(name = COLUMN_FOREIGN_ID)
    val bookId: String,//外键Book ID

    @Json(name = "authority")
    val authority: String
) : Parcelable {

    @Ignore
    fun fromAuthority(): String {
        if (authority == "CREATE") return "创建者"
        if (authority == "USER") return "用户"
        return authority
    }

    companion object {
        const val COLUMN_ID = "user_id"
        const val COLUMN_FOREIGN_ID = "book_id"
    }

}