package com.rh.heji.ui.bill.add.calendar

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.haibin.calendarview.Calendar
import com.rh.heji.App
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.internal.notify

class CalendarNoteViewModule : BaseViewModel() {
    val dataBase = AppDatabase.getInstance()
    val calendarLiveData = MutableLiveData<Map<String, Calendar>>()
    val dayBillsLiveData = MutableLiveData<Collection<BaseNode>>();
    var year: Int = thisYear
    var month: Int = thisMonth
    private val thisYear: Int
        private get() = java.util.Calendar.getInstance()[java.util.Calendar.YEAR]

    private val thisMonth: Int
        private get() = java.util.Calendar.getInstance()[java.util.Calendar.MONTH] + 1

    fun updateYearMonth(year: Int, month: Int) {
        launchIO({
            val bills = dataBase.billDao().findBillsBetweenTime(MyTimeUtils.getFirstDayOfMonth(year, month), MyTimeUtils.getLastDayOfMonth(year, month))
            var map = mutableMapOf<String, Calendar>()

            bills?.forEach {
                var calendar = java.util.Calendar.getInstance()
                calendar.time = it.billTime

                var thisYear = calendar.get(java.util.Calendar.YEAR)
                var thisMonth = calendar.get(java.util.Calendar.MONTH) + 1
                var thisDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                var time = com.blankj.utilcode.util.TimeUtils.date2String(it.billTime, "yyyy-MM-dd")
                var expenditure = dataBase.billDao().findIncomeByDay(time, BillType.EXPENDITURE.type().toString())
                        ?: "0"
                var income = dataBase.billDao().findIncomeByDay(time, BillType.INCOME.type().toString())
                        ?: "0"
                if (expenditure != "0" || income != "0") {
                    val calender: Calendar = getSchemeCalendar(thisYear, thisMonth, thisDay, expenditure, income)
                    map[calender.toString()] = calender
                }

            }

            calendarLiveData.postValue(map)
        }, {})
    }

    fun todayBills(calendar: Calendar) {
        LogUtils.i(calendar.toString())
        launchIO({
            val dateTime = TimeUtils.millis2String(calendar.timeInMillis, "yyyy-MM-dd")
            val dayBills = dataBase.billDao().findListByDay(dateTime)
            var expenditure = "0"
            var income = "0"
            calendar.schemes?.forEach {
                if (it.type == 1) {
                    income = it.obj as String
                } else {
                    expenditure = it.obj as String
                }
            }

            var weekDay = calendar.week
            var dayIncome = DayIncome(
                    expected = expenditure as String,
                    income = income as String,
                    month = calendar.month,
                    weekday = weekDay,
                    monthDay = calendar.day
            )

            var parentNode = mutableListOf<BaseNode>()
            var childNodes = emptyList<BaseNode>().toMutableList()
            dayBills.forEach {
                var billsNode = DayBillsNode(it)
                childNodes.add(billsNode)
            }
            parentNode.add(DayIncomeNode(childNodes, dayIncome))
            dayBillsLiveData.postValue(parentNode)
        }, {})
    }

    /**
     * 年
     * 月
     * 日
     * 支出
     * 收入
     */
    private fun getSchemeCalendar(year: Int, month: Int, day: Int, expenditure: String = "0", income: String = "0"): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        if (expenditure != "0") {
            val expenditureScheme = Calendar.Scheme()
            expenditureScheme.type = -1
            expenditureScheme.shcemeColor = App.getContext().getColor(R.color.expenditure)
            expenditureScheme.obj = "-$expenditure"
            calendar.addScheme(expenditureScheme)
        }

        if (income != "0") {
            val incomeScheme = Calendar.Scheme()
            incomeScheme.type = 1
            incomeScheme.shcemeColor = App.getContext().getColor(R.color.income)
            incomeScheme.obj = "+$income"
            calendar.addScheme(incomeScheme)
        }

        return calendar
    }
}