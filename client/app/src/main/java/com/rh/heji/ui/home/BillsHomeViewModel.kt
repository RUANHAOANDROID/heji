package com.rh.heji.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.Image
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.MyTimeUtils
import java.util.*

class BillsHomeViewModel : BaseViewModel() {

    var year: Int = thisYear //默认为当前时间
    var month: Int = thisMonth//默认为当前月份

    private val billDao: BillDao
    private var mBillLiveData: MediatorLiveData<List<Bill>>
    var billImagesLiveData: MediatorLiveData<List<Image>> = MediatorLiveData()

    init {
        mBillLiveData = MediatorLiveData<List<Bill>>()
        billDao = AppDatabase.getInstance().billDao()
    }

    /**
     * 调用getBills之前必须先设定year ,month
     *
     * @return 账单列表
     */
    val bills: LiveData<List<Bill>>
        get() {
            if (mBillLiveData == null) mBillLiveData = MediatorLiveData()
            val start = MyTimeUtils.getFirstDayOfMonth(year, month)
            LogUtils.d("Start time: ", start)
            val end = MyTimeUtils.getLastDayOfMonth(year, month)
            LogUtils.d("End time: ", end)
            val disposable = billDao.findBillsFollowableByTime(start, end)
            return Transformations.distinctUntilChanged(disposable)
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
        val start = MyTimeUtils.getFirstDayOfMonth(year, month)
        LogUtils.d("Start time: ", start)
        val end = MyTimeUtils.getLastDayOfMonth(year, month)
        LogUtils.d("End time: ", end)
        return Transformations.distinctUntilChanged(AppDatabase.getInstance().billDao().findTotalMoneyByTime(start, end, type))
    }

    private val thisYear: Int
        private get() = Calendar.getInstance()[Calendar.YEAR]
    private val thisMonth: Int
        private get() = Calendar.getInstance()[Calendar.MONTH] + 1
}