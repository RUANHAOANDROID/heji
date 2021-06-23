package com.rh.heji.utlis

import java.io.Serializable

/**
 *Date: 2021/5/11
 *Author: 锅得铁
 *#
 */
data class YearMonth(var year: Int, var month: Int):Serializable {

    override fun toString(): String {
        return "${year}-${if (month >= 10) month else "0$month"}"
    }
}