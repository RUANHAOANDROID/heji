package com.rh.heji.ui.bill.calendar

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.haibin.calendarview.Calendar
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.Image
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.YearMonth

class CalendarNoteViewModule : BaseViewModel() {
    val billDao: BillDao =   AppDatabase.getInstance().billDao()
    val calendarLiveData = MutableLiveData<Map<String, Calendar>>()
    val dayBillsLiveData = MutableLiveData<Collection<BaseNode>>()


    var selectYearMonth = YearMonth(
        java.util.Calendar.getInstance()[java.util.Calendar.YEAR],
        java.util.Calendar.getInstance()[java.util.Calendar.MONTH] + 1
    )

    fun updateYearMonth(year: Int, month: Int) {
        launchIO({
            var map = mutableMapOf<String, Calendar>()
            var everyDayIncome = billDao.findEveryDayIncomeByMonth(selectYearMonth.toString())
            everyDayIncome?.forEach { dayIncome ->
                var yymmdd = dayIncome.time!!.split("-")
                if (dayIncome.expenditure.toString() != "0" || dayIncome.income.toString() != "0") {
                    val calender: Calendar = getSchemeCalendar(
                        year = yymmdd[0].toInt(),
                        month = yymmdd[1].toInt(),
                        day = yymmdd[2].toInt(),
                        expenditure = dayIncome.expenditure.toString(),
                        income = dayIncome.income.toString()
                    )
                    //map["${dayIncome.time}-${dayIncome.income }${dayIncome.expenditure}"] = calender
                    map[calender.toString()] = calender// Key需是calendar string
                }
            }
            calendarLiveData.postValue(map)
            LogUtils.i(year, month, "$map")
        }, {})
    }

    fun todayBills(calendar: Calendar) {
        LogUtils.i(calendar.toString())
        launchIO({
            val dateTime = TimeUtils.millis2String(calendar.timeInMillis, "yyyy-MM-dd")
            val dayBills = billDao.findByDay(dateTime)
            dayBills?.let {
                var expenditure = "0"
                var income = "0"
                calendar.schemes?.forEach { scheme ->
                    if (scheme.type == 1) {
                        income = scheme.obj as String
                    } else {
                        expenditure = scheme.obj as String
                    }
                }
                var dayIncome = DayIncome(
                    expected = expenditure,
                    income = income,
                    year = calendar.year,
                    month = calendar.month,
                    monthDay = calendar.day,
                    weekday = calendar.week
                )
                var parentNode = mutableListOf<BaseNode>()
                var childNodes = emptyList<BaseNode>().toMutableList()
                it.forEach {
                    var billsNode = DayBillsNode(it)
                    childNodes.add(billsNode)
                }
                if (childNodes.size > 0) {
                    parentNode.add(DayIncomeNode(childNodes, dayIncome))
                }
                dayBillsLiveData.postValue(parentNode)
                LogUtils.i(dayIncome.toString())
            }
        }, {})
    }

    /**
     * 年
     * 月
     * 日
     * 支出
     * 收入
     */
    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int,
        expenditure: String = "0",
        income: String = "0"
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        if (expenditure != "0") {
            val expenditureScheme = Calendar.Scheme()
            expenditureScheme.type = -1
            expenditureScheme.shcemeColor = AppCache.instance.context.getColor(R.color.expenditure)
            expenditureScheme.obj = "-$expenditure"
            calendar.addScheme(expenditureScheme)
        }

        if (income != "0") {
            val incomeScheme = Calendar.Scheme()
            incomeScheme.type = 1
            incomeScheme.shcemeColor = AppCache.instance.context.getColor(R.color.income)
            incomeScheme.obj = "+$income"
            calendar.addScheme(incomeScheme)
        }

        return calendar
    }
}