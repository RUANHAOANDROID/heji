package com.rh.heji.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.query.Income
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.utlis.MyTimeUtils
import java.util.*

class BillsHomeViewModel : BaseViewModel() {

    var year: Int = Calendar.getInstance()[Calendar.YEAR] //默认为当前时间
    var month: Int = Calendar.getInstance()[Calendar.MONTH] + 1//默认为当前月份

    private val billDao: BillDao = AppDatabase.getInstance().billDao()
    var billImagesLiveData: MediatorLiveData<List<Image>> = MediatorLiveData()

    val billsNodLiveData = MediatorLiveData<MutableList<BaseNode>>()

    fun getBillsData() {
        launchIO({
            val start = MyTimeUtils.firstDayOfMonth(year, month)
            val end = MyTimeUtils.lastDayOfMonth(year, month)
            LogUtils.d("time: ", "$start - $end")
            var monthEveryDayIncome = billDao.findEveryDayIncome(start, end)
            var listDayNodes = mutableListOf<BaseNode>()
            monthEveryDayIncome?.forEach { dayIncome ->
                var yymmdd = dayIncome.time!!.split("-")
                var incomeNode = DayIncome(
                        expected = dayIncome.expenditure.toString(),
                        income = dayIncome.income.toString(),
                        year = yymmdd[0].toInt(),
                        month = yymmdd[1].toInt(),
                        monthDay = yymmdd[2].toInt(),
                        weekday = TimeUtils.getChineseWeek(dayIncome.time, TimeUtils.getSafeDateFormat(MyTimeUtils.PATTERN_DAY))
                )
                val dayListNodes = mutableListOf<BaseNode>()
                billDao.findListByDay(dayIncome.time)?.forEach {
                    dayListNodes.add(DayBillsNode(it))
                }
                var dayItemNode = DayIncomeNode(dayListNodes, incomeNode)
                listDayNodes.add(dayItemNode)
            }
            billsNodLiveData.postValue(listDayNodes)
        }, {})

    }

    fun getBillImages(billId: String): MediatorLiveData<List<Image>> {
        launchIO({
            billImagesLiveData.postValue(AppDatabase.getInstance().imageDao().findByBillId(billId))
        }, {
            it.printStackTrace()
        })
        return billImagesLiveData
    }

    fun getIncomeExpense(year: Int, month: Int): LiveData<Income> {
        val start = MyTimeUtils.firstDayOfMonth(year, month)
        val end = MyTimeUtils.lastDayOfMonth(year, month)
        LogUtils.d("time: ", "$start - $end")

        return Transformations.distinctUntilChanged(billDao.sumIncome(start, end))
    }
}