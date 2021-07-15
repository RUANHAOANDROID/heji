package com.rh.heji.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.AppCache
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.query.Income
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import java.util.*

class BillsHomeViewModel : BaseViewModel() {
    var selectYearMonth = YearMonth(
        Calendar.getInstance()[Calendar.YEAR], //默认为当前时间,
        Calendar.getInstance()[Calendar.MONTH] + 1//默认为当前月份
    )
    private val billDao: BillDao = AppDatabase.getInstance().billDao()

    private val billsNodLiveData = MediatorLiveData<MutableList<BaseNode>>()

    fun getBillsData(): LiveData<MutableList<BaseNode>> {
        launchIO({

            var monthEveryDayIncome = billDao.findEveryDayIncomeByMonth(
                AppCache.getInstance().currentBook.id,
                selectYearMonth.toString()
            )
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
        return billsNodLiveData
    }

    fun getIncomeExpense(): LiveData<Income> {
        LogUtils.d("Between by time:$selectYearMonth")
        return Transformations.distinctUntilChanged(billDao.sumIncome(selectYearMonth.toString()))
    }
}