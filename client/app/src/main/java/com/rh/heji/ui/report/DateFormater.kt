package com.rh.heji.ui.report

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 *Date: 2021/4/30
 *Author: 锅得铁
 *#
 */
class DateFormater : ValueFormatter() {
    companion object {
        val year = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return year[value.toInt()]
    }

}