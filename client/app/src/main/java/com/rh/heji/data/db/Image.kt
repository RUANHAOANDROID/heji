package com.rh.heji.data.db

import androidx.room.*
import java.util.*

/**
 * Date: 2020/11/19
 * Author: 锅得铁
 * #
 */
@Entity(
    tableName = "image",
    foreignKeys = [ForeignKey(
        entity = Bill::class,
        parentColumns = [Bill.COLUMN_ID],
        childColumns = [Image.COLUMN_ID],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Image(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = COLUMN_ID, index = true)
    var billImageID: String
) {



    var md5: String? = null
    var ext: String? = null

    @ColumnInfo(name = COLUMN_PATH)
    var localPath: String? = null

    @ColumnInfo(name = COLUMN_ONLINE_PATH)
    var onlinePath: String? = null

    @ColumnInfo(name = COLUMN_STATUS, defaultValue = "0")
    var synced = 0

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val image = o as Image
        return id === image.id && billImageID == image.billImageID &&
                localPath == image.localPath &&
                onlinePath == image.onlinePath
    }

    override fun hashCode(): Int {
        return Objects.hash(id, billImageID, localPath, onlinePath)
    }

    override fun toString(): String {
        return "Image{" +
                "_id='" + id + '\'' +
                ", bill_id='" + billImageID + '\'' +
                ", md5='" + md5 + '\'' +
                ", ext='" + ext + '\'' +
                ", localPath='" + localPath + '\'' +
                ", onlinePath='" + onlinePath + '\'' +
                ", synced=" + synced +
                '}'
    }

    companion object {
        const val COLUMN_ID = "_bid"
        const val COLUMN_PATH = "img_path"
        const val COLUMN_ONLINE_PATH = "img_online_path"
        const val COLUMN_STATUS = "sync_status"
    }
}