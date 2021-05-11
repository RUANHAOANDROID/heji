package com.rh.heji.utlis

/**
 *Date: 2021/5/11
 *Author: 锅得铁
 *#
 */
data class YearMonth(var year: Int, var month: Int) {

    override fun toString(): String {
        return "${year}-${if (month >= 10) month else "0$month"}"
    }
}