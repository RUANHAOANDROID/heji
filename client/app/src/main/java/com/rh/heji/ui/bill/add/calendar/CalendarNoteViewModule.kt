package com.rh.heji.ui.bill.add.calendar

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.haibin.calendarview.Calendar
import com.rh.heji.App
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.Image
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils

class CalendarNoteViewModule : BaseViewModel() {
    val billDao: BillDao = AppDatabase.getInstance().billDao()
    val calendarLiveData = MutableLiveData<Map<String, Calendar>>()
    val dayBillsLiveData = MutableLiveData<Collection<BaseNode>>()
    private val billImageLiveData = MutableLiveData<List<Image>>()


    var year: Int = thisYear
    var month: Int = thisMonth
    private val thisYear: Int
        private get() = java.util.Calendar.getInstance()[java.util.Calendar.YEAR]

    private val thisMonth: Int
        private get() = java.util.Calendar.getInstance()[java.util.Calendar.MONTH] + 1

    fun updateYearMonth(year: Int, month: Int) {
        launchIO({
            val haveBillDays = billDao.findHaveBillDays(MyTimeUtils.firstDayOfMonth(year, month), MyTimeUtils.lastDayOfMonth(year, month))
            var map = mutableMapOf<String, Calendar>()

            haveBillDays?.forEach { time ->
                var calendar = java.util.Calendar.getInstance()
                calendar.time = TimeUtils.string2Date(time, "yyyy-MM-dd")
                var thisYear = calendar.get(java.util.Calendar.YEAR)
                var thisMonth = calendar.get(java.util.Calendar.MONTH) + 1
                var thisDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
//                var expenditure = billDao.findIncomeByDay(time, BillType.EXPENDITURE.typeString())
//                        ?: "0"
//                var income = billDao.findIncomeByDay(time, BillType.INCOME.typeString()) ?: "0"
                var income =billDao.sumDayIncome(time);
                if (income.expenditure.toString() != "0" || income.income.toString() != "0") {
                    val calender: Calendar = getSchemeCalendar(thisYear, thisMonth, thisDay, income.expenditure.toString(), income.income.toString())
                    map[calender.toString()] = calender
                }
            }
            if (map.isNotEmpty()) {
                calendarLiveData.postValue(map)
            }
            LogUtils.i(year, month, "$map")
        }, {})
    }

    fun todayBills(calendar: Calendar) {
        LogUtils.i(calendar.toString())
        launchIO({
            val dateTime = TimeUtils.millis2String(calendar.timeInMillis, "yyyy-MM-dd")
            val dayBills = billDao.findListByDay(dateTime)
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
                        weekday = calendar.week)
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

    fun getBillImages(billId: String): MutableLiveData<List<Image>> {
        launchIO({
            billImageLiveData.postValue(AppDatabase.getInstance().imageDao().findByBillId(billId));
        }, {})
        return billImageLiveData
    }


}