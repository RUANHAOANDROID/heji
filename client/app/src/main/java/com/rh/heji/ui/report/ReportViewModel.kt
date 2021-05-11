package com.rh.heji.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.github.mikephil.charting.data.PieEntry
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.YearMonth
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
    var yearMonth: YearMonth= YearMonth(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH))
        set(value) {
            field = value
            categoryProportion(field)
            LogUtils.d(value)
        }

    fun getIncome(date: String = "0", month: String = "0") {

    }

    fun getExpenditure(date: String = "0", month: String = "0") {

    }

    private var category = MutableLiveData<List<PieEntry>>()

    /**
     * 分类所占百分比
     * 分类所占金额和百分比
     */
    fun categoryProportion(date: YearMonth): LiveData<List<PieEntry>> {
        if (isYear) {

        } else {

        }
        LogUtils.d(date)
        launchIO({
            val list = AppDatabase.getInstance().billDao().reportCategory(-1, yearMonth.toString()).stream().map {
                return@map PieEntry(it.percentage, it.category, it.money)
            }.collect(Collectors.toList())
            category.postValue(list)
            LogUtils.d(list)
        }, {})
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