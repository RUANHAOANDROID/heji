package com.rh.heji.data.db

const val STATUS_SYNCED = 1 //已同步的
const val STATUS_DELETE = -1 //本地删除的
const val STATUS_NOT_SYNC = 0 //未同步的
const val STATUS_UPDATE = 2 //已更改的

const val NOT_REDELETE = "sync_status!=$STATUS_DELETE"
const val YEARMONTH = "strftime('%Y-%m',\${})"