package com.rh.heji.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.github.mikephil.charting.data.PieEntry
import com.rh.heji.currentYearMonth
import com.rh.heji.App
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.dto.Income
import com.rh.heji.data.db.dto.IncomeTimeSurplus
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.KeyValue
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import java.math.BigDecimal
import java.util.*

class ReportViewModel : BaseViewModel() {

    private val incomeExpenditureLiveData: MutableLiveData<Income> = MutableLiveData()//发射器
    val incomeExpenditure: LiveData<Income>
        get() = incomeExpenditureLiveData

    private var categoryLiveData = MutableLiveData<MutableList<PieEntry>>()
    val categoryProportion: LiveData<MutableList<PieEntry>>
        get() = categoryLiveData

    private var reportBillsLiveData = MutableLiveData<MutableList<IncomeTimeSurplus>>()
    val reportBillsList: LiveData<MutableList<IncomeTimeSurplus>>
        get() = reportBillsLiveData

    private var everyNodeIncomeExpenditureLiveData = MutableLiveData<KeyValue>()
    val everyNodeIncomeExpenditure: LiveData<KeyValue>
        get() {
            expenditure()
            return everyNodeIncomeExpenditureLiveData
        }

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
            LogUtils.d("Type $value")
        }

    /**
     * 日期
     */
    var yearMonth: YearMonth = currentYearMonth
        set(value) {
            field = value
            monthIncomeExpenditure()
            expenditure()
            monthCategoryProportion(BillType.EXPENDITURE)
            monthReportList()

            LogUtils.d(value)
        }

    fun refreshData(billType: BillType) {
        monthIncomeExpenditure()
        expenditure()
        monthCategoryProportion(billType)
        monthReportList()
    }

    private fun monthIncomeExpenditure() {
        launchIO({
            val monthIncomeExpenditureData =
                App.dataBase.billDao()
                    .sumMonthIncomeExpenditure(yearMonth.toYearMonth())
            incomeExpenditureLiveData.postValue(monthIncomeExpenditureData)
        }, {})

    }

    fun income() {
        launchIO({
            val keyValue = KeyValue(
                BillType.INCOME.type(),
                App.dataBase.billDao()
                    .sumByMonth(yearMonth.toYearMonth(), BillType.INCOME.type())
            )
            everyNodeIncomeExpenditureLiveData.postValue(keyValue)
        }, {})
    }

    fun expenditure() {
        launchIO({
            val data = KeyValue(
                BillType.EXPENDITURE.type(),
                App.dataBase.billDao()
                    .sumByMonth(yearMonth.toYearMonth(), BillType.EXPENDITURE.type())
            )
            everyNodeIncomeExpenditureLiveData.postValue(data)
            LogUtils.d(data)
        }, {})
    }

    fun incomeAndExpenditure() {
        launchIO({
            val arrays = arrayListOf(
                App.dataBase.billDao()
                    .sumByMonth(yearMonth.toYearMonth(), BillType.EXPENDITURE.type()),
                App.dataBase.billDao()
                    .sumByMonth(yearMonth.toYearMonth(), BillType.INCOME.type())
            )
            val data = KeyValue(
                BillType.ALL.type(),
                arrays
            )
            everyNodeIncomeExpenditureLiveData.postValue(data)
            LogUtils.d()
        }, {})
    }

    /**
     * 分类所占百分比
     * 分类所占金额和百分比
     */
    fun monthCategoryProportion(billType: BillType) {
        launchIO({
            val list =
                App.dataBase.billDao().reportCategory(billType.type(), yearMonth.toYearMonth())
                    .map {
                        // 支出为负数，收入为正数  money * -1 or money * 1
                        val data = it.money!!.multiply(BigDecimal(billType.type()))
                        return@map PieEntry(
                            it.percentage, it.category, data
                        )
                    }.toMutableList()
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
            var data = App.dataBase.billDao()
                .listIncomeExpSurplusByMonth(yearMonth.toYearMonth())
            reportBillsLiveData.postValue(data)
        }, {})

    }

}