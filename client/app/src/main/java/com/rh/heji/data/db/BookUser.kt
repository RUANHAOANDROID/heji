package com.rh.heji.data.db

import android.os.Parcelable
import androidx.room.*
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

    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name = COLUMN_ID)
    @Transient//moshi 忽略字段
    val id: Long = 0,

    //主键也是用户名
    @Json(name = "name")
    var name: String,
    @Transient//moshi 忽略字段
    @ColumnInfo(name = COLUMN_FOREIGN_ID)
    var bookId: String? = null,//外键Book ID

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