package com.rh.heji.data.db

interface Constant {
    companion object {
        const val STATUS_SYNCED = 1 //已同步的
        const val STATUS_DELETE = -1 //本地删除的
        const val STATUS_NOT_SYNC = 0 //未同步的
        const val STATUS_UPDATE = 2 //已更改的
    }
}