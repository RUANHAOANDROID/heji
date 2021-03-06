package com.rh.heji.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.github.mikephil.charting.data.PieEntry
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.query.Income
import com.rh.heji.data.db.query.IncomeTimeSurplus
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import java.util.*
import java.util.stream.Collectors

class ReportViewModel : BaseViewModel() {

    private val incomeExpenditureLiveData: MutableLiveData<Income> = MutableLiveData()//发射器
    val incomeExpenditure: LiveData<Income>
        get() = incomeExpenditureLiveData

    private var categoryLiveData = MutableLiveData<List<PieEntry>>()
    val categoryProportion: LiveData<List<PieEntry>>
        get() = categoryLiveData

    private var reportBillsLiveData = MutableLiveData<MutableList<IncomeTimeSurplus>>()
    val reportBillsList: LiveData<MutableList<IncomeTimeSurplus>>
        get() = reportBillsLiveData

    private var everyNodeIncomeExpenditureLiveData = MutableLiveData<List<Bill>>()
    val everyNodeIncomeExpenditure: LiveData<List<Bill>>
        get() = everyNodeIncomeExpenditureLiveData


    init {

    }

    var isAllYear: Boolean = false

    /**
     * 是否是全年统计
     */
    var allYear = Calendar.getInstance().get(Calendar.YEAR)
        set(value) {
            field = value
            LogUtils.d("$value 全年")
        }

    /**
     * 统计类型
     */
    var totalType = BillType.EXPENDITURE.type()
        set(value) {
            field = value
            isAllYear = true
            LogUtils.d("Type $value")
        }

    /**
     * 日期
     */
    var yearMonth: YearMonth = YearMonth(
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH) + 1
    )
        set(value) {
            field = value
            isAllYear = false
            monthIncomeExpenditure()
            monthEveryNodeIncomeExpenditure()
            monthCategoryProportion()
            monthReportList()

            LogUtils.d(value)
        }

    fun refreshData(type: Int) {
        monthIncomeExpenditure()
        monthEveryNodeIncomeExpenditure()
        monthCategoryProportion()
        monthReportList()
    }
    private fun monthIncomeExpenditure() {
        launchIO({
            val monthIncomeExpenditureData =
                  AppDatabase.getInstance().billDao().sumMonthIncomeExpenditure(yearMonth.toString())
            incomeExpenditureLiveData.postValue(monthIncomeExpenditureData)
        }, {})

    }

    private fun monthEveryNodeIncomeExpenditure() {
        launchIO({
            everyNodeIncomeExpenditureLiveData.postValue(
                  AppDatabase.getInstance().billDao().findByMonth(yearMonth.toString())
            )
        }, {})

    }

    /**
     * 分类所占百分比
     * 分类所占金额和百分比
     */
    private fun monthCategoryProportion() {
        launchIO({
            val list =   AppDatabase.getInstance().billDao().reportCategory(-1, yearMonth.toString())
                .stream().map {
                    return@map PieEntry(it.percentage, it.category, it.money)
                }.collect(Collectors.toList())
            categoryLiveData.postValue(list)
            LogUtils.d(list)
        }, {})
    }

    /**
     * 月报表（每日统计）
     * 日期|收入|支出|结余
     *
     */
    fun yearReportList() {

    }


    /**
     * 月报表（一年中每个月）
     * 月份|收入|支出|结余
     * @date yyyy-mm
     */
    private fun monthReportList() {
        launchIO({
            var data =   AppDatabase.getInstance().billDao()
                .listIncomeExpSurplusByMonth(yearMonth.toString())
            reportBillsLiveData.postValue(data)
        }, {})

    }

}