package com.hao.heji.data.db


/**
 * 狀態
 */
object STATUS {
    const val SYNCED = 0 //已同步的
    const val NEW = 1 //新创建未同步的
    const val DELETED = 2 //本地删除的
    const val UPDATED = 3 //已更改的(本地更改后需要再次同步到服务器)
}

//----條件
const val YEAR = "%Y"
const val MONTH = "%Y-%m"
const val YEARMONTHDAY = "%Y-%m-%d"
const val NOT_REDELETE = "sync_status!=${STATUS.DELETED}"//非本地預刪除的

fun dateFormat(year: String) = year
fun dateFormat(year: String, month: String) = "${year}-$month"
fun dateFormat(year: Int, month: Int, day: String) = "${year}-$month-$day"