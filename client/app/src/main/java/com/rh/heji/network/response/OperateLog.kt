package com.rh.heji.network.response

data class OperateLog(val targetId: String, val type: Int, val optClass: Int) {
    companion object {
        const val DELETE = 0
        const val UPDATE = 1

        const val BOOK = 0
        const val BILL = 1
        const val CATEGORY = 2
    }
}