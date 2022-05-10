package com.rh.heji.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.currentYearMonth
import com.rh.heji.App
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.dto.Income
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.launchIO

class BillListViewModel : BaseViewModel() {
    var selectYearMonth = currentYearMonth
    private val billDao: BillDao by lazy { App.dataBase.billDao()}


    private val billsNodLiveData = MediatorLiveData<MutableList<BaseNode>>()
    fun monthDataChange(): LiveData<MutableList<BaseNode>> = billsNodLiveData
    fun refreshMonthData() {
        launchIO({
            //根据月份查询收入的日子
            var monthEveryDayIncome =
                billDao.findEveryDayIncomeByMonth(yearMonth = selectYearMonth.toYearMonth())
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
            LogUtils.d("Select YearMonth:${selectYearMonth}${listDayNodes}")
            billsNodLiveData.postValue(listDayNodes)
        }, {})
    }

    fun getIncomeExpense(): LiveData<Income> {
        LogUtils.d("Between by time:$selectYearMonth")
        return billDao.sumIncome(selectYearMonth.toYearMonth())
            .asLiveData(viewModelScope.coroutineContext)
    }
}