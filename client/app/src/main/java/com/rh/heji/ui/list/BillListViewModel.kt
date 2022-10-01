package com.rh.heji.ui.list

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.App
import com.rh.heji.currentYearMonth
import com.rh.heji.data.db.BillDao
import com.rh.heji.ui.base.BaseViewModelMVI
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import kotlinx.coroutines.flow.collect

class BillListViewModel : BaseViewModelMVI<BillListAction, BillListUiState>() {

    private var selectYearMonth = currentYearMonth

    fun yearMonth(): YearMonth {
        return selectYearMonth
    }

    private val billDao: BillDao by lazy { App.dataBase.billDao() }

    override fun doAction(action: BillListAction) {
        super.doAction(action)
        when (action) {
            is BillListAction.Refresh -> {
                val yearMonth = yearMonth().yearMonthString()
                getMonthBills(yearMonth)
                getSummary(yearMonth)
            }
            is BillListAction.MonthBill -> {
                selectYearMonth = action.yearMonth
                getMonthBills(selectYearMonth.yearMonthString())
            }
            is BillListAction.Summary -> {
                getSummary(yearMonth = action.yearMonth.yearMonthString())
            }
        }
    }

    /**
     * yyyy-mm
     */
    private fun getMonthBills(yearMonth: String) {
        launchIO({
            //根据月份查询收入的日子
            var monthEveryDayIncome =
                billDao.findEveryDayIncomeByMonth(yearMonth = yearMonth)
            //日节点
            var listDayNodes = mutableListOf<BaseNode>()
            monthEveryDayIncome.forEach { dayIncome ->
                var yymmdd = dayIncome.time!!.split("-")
                var incomeNode = DayIncome(
                    expected = dayIncome.expenditure.toString(),
                    income = dayIncome.income.toString(),
                    year = yymmdd[0].toInt(),
                    month = yymmdd[1].toInt(),
                    monthDay = yymmdd[2].toInt(),
                    weekday = TimeUtils.getChineseWeek(
                        dayIncome.time,
                        TimeUtils.getSafeDateFormat(MyTimeUtils.PATTERN_DAY)
                    )
                )
                //日节点下子账单
                val dayListNodes = mutableListOf<BaseNode>()
                billDao.findByDay(dayIncome.time!!).forEach {
                    it.images = App.dataBase.imageDao().findImagesId(it.id)
                    dayListNodes.add(DayBillsNode(it))
                }
                var dayItemNode = DayIncomeNode(dayListNodes, incomeNode)
                listDayNodes.add(dayItemNode)
            }
            LogUtils.d("Select YearMonth:${yearMonth}${listDayNodes}")
            uiState.postValue(BillListUiState.Bills(listDayNodes))
        }, {
            uiState.postValue(BillListUiState.Error(it))
        })
    }

    /**
     * 获取收入支出 总览
     * @param yearMonth yyyy:mm
     */
    private fun getSummary(yearMonth: String) {
        launchIO({
            LogUtils.d("Between by time:$yearMonth")
            billDao.sumIncome(yearMonth).collect {
                uiState.postValue(BillListUiState.Summary(it))
            }
        }, {
            uiState.postValue(BillListUiState.Error(it))
        })

    }
}