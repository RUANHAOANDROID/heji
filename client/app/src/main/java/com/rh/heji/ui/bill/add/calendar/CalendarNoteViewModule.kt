package com.rh.heji.ui.bill.add.calendar

import android.util.TimeUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.haibin.calendarview.Calendar
import com.rh.heji.App
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.MyTimeUtils
import java.math.BigDecimal

class CalendarNoteViewModule : BaseViewModel() {
    val dataBase = AppDatabase.getInstance()
    val calendarLiveData = MediatorLiveData<Map<String, Calendar>>()
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
                var expenditure = dataBase.billDao().findDayIncome(time, BillType.EXPENDITURE.type().toString())
                        ?: "0"
                var income = dataBase.billDao().findDayIncome(time, BillType.INCOME.type().toString())
                        ?: "0"
                if (expenditure != "0" || income != "0") {
                    val calender: Calendar = getSchemeCalendar(thisYear, thisMonth, thisDay, expenditure, income)
                    map[calender.toString()] = calender
                }

            }
            calendarLiveData.postValue(map)
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