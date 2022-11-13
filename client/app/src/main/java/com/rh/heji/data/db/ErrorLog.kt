package com.rh.heji.data.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.rh.heji.data.db.mongo.ObjectId
import com.squareup.moshi.Json
import org.jetbrains.annotations.NotNull

/**
 * @date: 2021/3/2
 * @author: 锅得铁
 * #
 */
@Entity(tableName = "error_log")
data class ErrorLog(
    @PrimaryKey
    @NotNull var id: String
) {


    @Json(name = "timeOfCrash")
    var timeOfCrash: Long = System.currentTimeMillis()

    @Json(name = "uid")
    var userid: String? = null
    var deviceModel: String? = null
    var sdkVersionName: String? = null
    var sdkVersionCode: String? = null
    var appVersionName: String? = null
    var appVersionCode: String? = null
    var isTablet = false
    var isEmulator = false
    var uniqueDeviceId: String? = null
    var networkType: String? = null
    var crashContent: String? = null
    @Ignore
    constructor() : this(id = ObjectId().toHexString())
}