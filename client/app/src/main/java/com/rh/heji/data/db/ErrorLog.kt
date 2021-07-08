package com.rh.heji.data.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rh.heji.data.db.mongo.ObjectId
import org.jetbrains.annotations.NotNull

/**
 * Date: 2021/3/2
 * Author: 锅得铁
 * #
 */
@Entity(tableName = "error_log")
data class ErrorLog(
    @PrimaryKey
    @NotNull var id: String
) {


    @SerializedName("timeOfCrash")
    var timeOfCrash: Long = System.currentTimeMillis()

    @SerializedName("uid")
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

    constructor() : this(id = ObjectId().toHexString()){}
}