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
const val NOT_REDELETE = "sync_status!=${STATUS.DELETED}"//本地預刪除的
const val YEARMONTH = "strftime('%Y-%m',\${})"
