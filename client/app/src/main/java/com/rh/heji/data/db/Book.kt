package com.rh.heji.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rh.heji.data.db.mongo.ObjectId
import com.squareup.moshi.Json
import org.jetbrains.annotations.NotNull

/**
 * Date: 2021/7/8
 * Author: 锅得铁
 * #
 */
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
    var bannerUrl: String? = null
) {
    @ColumnInfo(name = "sync_status")
    var synced: Int = STATUS.NOT_SYNCED

    companion object {
        const val COLUMN_ID = "id"
    }
}