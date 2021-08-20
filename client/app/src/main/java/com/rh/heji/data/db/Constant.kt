package com.rh.heji.data.db


/**
 * 狀態
 */
object STATUS {
    const val SYNCED = 1 //已同步的
    const val DELETED = -1 //本地删除的
    const val NOT_SYNCED = 0 //未同步的
    const val UPDATED = 2 //已更改的
}

//----條件
const val YEAR = "%Y"
const val MONTH = "%Y-%m"
const val YEARMONTHDAY = "%Y-%m-%d"
const val NOT_REDELETE = "sync_status!=${STATUS.DELETED}"//本地預刪除的
const val YEARMONTH = "strftime('%Y-%m',\${})"

fun dateFormat(year: String) = "$year"
fun dateFormat(year: String, month: String) = "${year}-$month"
fun dateFormat(year: Int, month: Int, day: String) = "${year}-$month-$day"