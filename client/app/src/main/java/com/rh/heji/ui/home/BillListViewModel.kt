package com.rh.heji.ui.home

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.App
import com.rh.heji.currentYearMonth
import com.rh.heji.data.db.BillDao
import com.rh.heji.ui.adapter.DayBillsNode
import com.rh.heji.ui.adapter.DayIncome
import com.rh.heji.ui.adapter.DayIncomeNode
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utils.MyTimeUtils
import com.rh.heji.utils.YearMonth
import com.rh.heji.utils.launchIO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

internal class BillListViewModel : BaseViewModel<BillListAction, BillListUiState>() {

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
            is BillListAction.GetImages -> {
                getImages(action.bid)
            }
        }
    }

    private fun getImages(billId: String) {
        launchIO({
            val data = App.dataBase.imageDao().findByBillId(billId = billId)
            send(BillListUiState.Images(data))
        })
    }

    /**
     * yyyy-mm
     */
    private fun getMonthBills(yearMonth: String) {
        launchIO({
            //根据月份查询收支的日子
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
            LogUtils.d("Select YearMonth:${yearMonth} ${listDayNodes.size}")
            send(BillListUiState.Bills(listDayNodes))
        }, {
            send(BillListUiState.Error(it))
        })
    }

    /**
     * 获取收入支出 总览
     * @param yearMonth yyyy:mm
     */
    private fun getSummary(yearMonth: String) {
        launchIO({
            LogUtils.d("Between by time:$yearMonth")
            billDao.sumIncome(yearMonth).distinctUntilChanged().collect {
                LogUtils.d(it)
                send(BillListUiState.Summary(it))
            }
        }, {
            send(BillListUiState.Error(it))
        })

    }
}