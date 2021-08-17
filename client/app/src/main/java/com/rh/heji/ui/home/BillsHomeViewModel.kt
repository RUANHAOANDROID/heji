package com.rh.heji.ui.home

import androidx.lifecycle.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.AppCache
import com.rh.heji.currentYearMonth
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.d2o.Income
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

class BillsHomeViewModel : BaseViewModel() {
    var selectYearMonth = currentYearMonth
    private val billDao: BillDao = AppDatabase.getInstance().billDao()

    private val billsNodLiveData = MediatorLiveData<MutableList<BaseNode>>()
    fun monthDataChange(): LiveData<MutableList<BaseNode>> = billsNodLiveData
    fun refreshMonthData() {
        launchIO({
            var monthEveryDayIncome =
                billDao.findEveryDayIncomeByMonth(yearMonth = selectYearMonth.toYearMonth())
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
                val dayListNodes = mutableListOf<BaseNode>()
                billDao.findByDay(dayIncome.time!!).forEach {
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