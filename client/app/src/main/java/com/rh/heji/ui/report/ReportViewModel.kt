package com.rh.heji.ui.report

import com.blankj.utilcode.util.LogUtils
import com.github.mikephil.charting.data.PieEntry
import com.rh.heji.App
import com.rh.heji.currentYearMonth
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.STATUS
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import java.math.BigDecimal

/**
 * 统计ViewModel
 *
 */
class ReportViewModel : BaseViewModel<ReportAction, ReportUiState>() {
    /**
     * 日期
     */
    var yearMonth: YearMonth = currentYearMonth
        set(value) {
            isAnnual = value.isYear()
            field = value
            LogUtils.d("${field.yearString()}annual : $value ")
        }

    /**
     * Annual 年度报表，默认月报表
     */
    private var isAnnual: Boolean = false

    private var lineDataType: Int = BillType.EXPENDITURE.valueInt()

    private var pieDataType: Int = BillType.EXPENDITURE.valueInt()

    override fun doAction(action: ReportAction) {
        super.doAction(action)
        when (action) {
            is ReportAction.SelectTime -> {
                yearMonth = action.yearMonth
                total()
                getLinChartData()
                getProportionChart()
                getReportList()
            }
            is ReportAction.Total -> {
                total()
            }
            is ReportAction.GetLinChartData -> {
                val type = action.type
                getLinChartData(type)
            }
            is ReportAction.GetProportionChart -> {
                val type = action.type
                getProportionChart(type)
            }
            is ReportAction.GetCategoryBillList -> {
                val type = action.type
                val category = action.category
                getCategoryBills(category, type)
            }
            is ReportAction.GetReportList -> {
                getReportList()
            }
            is ReportAction.GetReportBillInfoList -> {
                val ymd = action.yearMonth.yearMonthDayString()
                getReportBillInfoList(ymd)
            }
            is ReportAction.GetImages -> {
                getImages(action.img_ids)
            }
        }
    }

    private fun getReportBillInfoList(ymd: String) {
        launchIO({
            var data: MutableList<Bill> = mutableListOf()
            if (yearMonth.day == 0) {//按月查
                //TODO 目前暂未实现按年统计
                data = App.dataBase.billDao().findByMonth(ymd, type = null, App.currentBook.id)
                    .filter { bill ->
                        bill.images = App.dataBase.imageDao().findImagesId(bill.id)
                        return@filter true
                    }.toMutableList()
                send(ReportUiState.ReportBillInfoList(yearMonth.yearMonthString(), data))
            } else {//按天查
                data = App.dataBase.billDao().findByDay(ymd).filter {
                    it.images = App.dataBase.imageDao().findImagesId(it.id)
                    return@filter true
                }.toMutableList()
                send(ReportUiState.ReportBillInfoList(yearMonth.monthDayString(), data))
            }
        })
    }

    private fun getCategoryBills(category: String, type: Int) {
        launchIO({
            val bills = App.dataBase.billDao().findByCategoryAndMonth(
                category, yearMonth.yearMonthString(), type
            ).filter {
                it.images = App.dataBase.imageDao().findImagesId(it.id)
                return@filter true
            }.toMutableList()
            send(ReportUiState.CategoryList(category, bills))
        })
    }

    private fun getImages(imagesIDs: MutableList<String>) {
        launchIO({
            val data = App.dataBase.imageDao().findImage(imagesIDs)
            send(ReportUiState.Images(data))
        })
    }

    private fun getReportList() {
        launchIO({
            var data =
                App.dataBase.billDao().listIncomeExpSurplusByMonth(yearMonth.yearMonthString())
            send(ReportUiState.ReportList(data))
        })
    }

    private fun getProportionChart(type: Int = pieDataType) {
        launchIO({
            val list =
                App.dataBase.billDao().reportCategory(type, yearMonth.yearMonthString()).map {
                    // 支出为负数，收入为正数  money * -1 or money * 1
                    val data = it.money!!.multiply(BigDecimal(type))
                    return@map PieEntry(
                        it.percentage, it.category, data
                    )
                }.toMutableList()
            send(ReportUiState.ProportionChart(type, list))
        })
    }

    private fun getLinChartData(type: Int = lineDataType) {
        launchIO({
            if (type == BillType.ALL.valueInt()) {
                val uiStateData = with(ReportUiState.LinChart(type)) {
                    all = arrayListOf(
                        App.dataBase.billDao().sumByMonth(
                            yearMonth.yearMonthString(), BillType.EXPENDITURE.valueInt()
                        ), App.dataBase.billDao().sumByMonth(
                            yearMonth.yearMonthString(), BillType.INCOME.valueInt()
                        )
                    )
                    this
                }
                send(uiStateData)
            } else {
                val data = App.dataBase.billDao().sumByMonth(yearMonth.yearMonthString(), type)
                send(ReportUiState.LinChart(type, data))
            }
        })
    }

    private fun total() {
        launchIO({
            val monthIncomeExpenditureData =
                App.dataBase.billDao().sumMonthIncomeExpenditure(yearMonth.yearMonthString())
            send(ReportUiState.Total(monthIncomeExpenditureData))
        })
    }
}