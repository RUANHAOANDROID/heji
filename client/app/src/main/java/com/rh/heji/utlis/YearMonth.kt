package com.rh.heji.utlis

import java.io.Serializable

/**
 *Date: 2021/5/11
 *Author: 锅得铁
 *#
 */
data class YearMonth(var year:Int, var month: Int =0):Serializable {

    override fun toString(): String {
        if (month==0) return "$year"
        return "${year}-${if (month >= 10) month else "0$month"}"
    }
    //当month =0 代表全年
    fun isYear():Boolean=month==0
}