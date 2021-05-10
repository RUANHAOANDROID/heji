package com.rh.heji.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.rh.heji.data.AppDatabase
import com.rh.heji.ui.base.BaseViewModel
import java.util.*
import java.util.stream.Collectors

class ReportViewModel : BaseViewModel() {
    private val mText: MutableLiveData<String> = MutableLiveData()
    val text: LiveData<String>
        get() = mText

    init {
        mText.value = "This is gallery fragment"
    }

    /**
     * 是否是全年统计
     */
    var isYear = false

    /**
     * 日期
     */
    var date = ""

    fun getIncome(date: String = "0", month: String = "0") {

    }

    fun getExpenditure(date: String = "0", month: String = "0") {

    }
    private var category =MutableLiveData<List<PieEntry>>()
    /**
     * 分类所占百分比
     * 分类所占金额和百分比
     */
    fun categoryProportion(): LiveData<List<PieEntry>> {
        //launchIO({
            val list = AppDatabase.getInstance().billDao().reportCategory(-1, "2021-05").stream().map {
                return@map PieEntry(it.percentage, it.category)
            }.collect(Collectors.toList())
            category.value=list
        //}, {})
        return category
    }

    /**
     * 日报表
     * 日期|收入|支出|结余
     */
    fun dayReportList() {

    }

    /**
     * 月报表
     * 月份|收入|支出|结余
     * @date yyyy-mm
     */
    fun monthReportList(date: String) {
        //AppDatabase.getInstance().billDao().reportMonthList(date)
    }
}