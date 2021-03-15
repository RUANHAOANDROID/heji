package com.rh.heji.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.Image
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils
import java.util.*

class BillsHomeViewModel : BaseViewModel() {

    var year: Int = thisYear //默认为当前时间
    var month: Int = thisMonth//默认为当前月份

    private val billDao: BillDao = AppDatabase.getInstance().billDao()
    var billImagesLiveData: MediatorLiveData<List<Image>> = MediatorLiveData()
    var billsMap = mutableListOf<BaseNode>()
    val billsNodLiveData = MediatorLiveData<MutableList<BaseNode>>()

    fun getBillsData() {
        val start = MyTimeUtils.firstDayOfMonth(year, month)
        LogUtils.d("Start time: ", start)
        val end = MyTimeUtils.lastDayOfMonth(year, month)
        LogUtils.d("End time: ", end)
        val haveBillDays = billDao.findHaveBillDays(MyTimeUtils.firstDayOfMonth(year, month), MyTimeUtils.lastDayOfMonth(year, month))
        var listDayNodes = mutableListOf<BaseNode>()
        haveBillDays?.forEach { time ->
            var calendar = Calendar.getInstance()
            calendar.time = TimeUtils.string2Date(time, "yyyy-MM-dd")
            var currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            var dayIncome = billDao.sumDayIncome(time)
            var list = billDao.findListByDay(time)
            var incomeNode = DayIncome(
                    expected = dayIncome.expenditure,
                    income = dayIncome.income,
                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH) + 1,
                    monthDay = calendar.get(Calendar.DAY_OF_MONTH),
                    weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1
            )
            val dayListNodes = mutableListOf<BaseNode>()
            list.forEach {
                dayListNodes.add(DayBillsNode(it))
            }
            var dayItemNode = DayIncomeNode(dayListNodes, incomeNode)
            listDayNodes.add(dayItemNode)
        }
        billsNodLiveData.postValue(listDayNodes)
    }

    fun getBillImages(billId: String): MediatorLiveData<List<Image>> {
        launchIO({
            billImagesLiveData.postValue(AppDatabase.getInstance().imageDao().findByBillId(billId))
        }, {
            it.printStackTrace()
        })
        return billImagesLiveData
    }


    fun getIncomesOrExpenses(year: Int, month: Int, type: Int): LiveData<Double> {
        val start = MyTimeUtils.firstDayOfMonth(year, month)
        LogUtils.d("Start time: ", start)
        val end = MyTimeUtils.lastDayOfMonth(year, month)
        LogUtils.d("End time: ", end)
        //var list = billDao.sumDayIncome("2021-03-06");
        return Transformations.distinctUntilChanged(billDao.findTotalMoneyByTime(start, end, type))
    }

    private val thisYear: Int
        private get() = Calendar.getInstance()[Calendar.YEAR]
    private val thisMonth: Int
        private get() = Calendar.getInstance()[Calendar.MONTH] + 1
}