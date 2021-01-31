package com.rh.heji.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.BillDao
import com.rh.heji.data.db.Image
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.MyTimeUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.stream.Collectors

class BillsHomeViewModel : BaseViewModel() {

    var year: Int = thisYear //默认为当前时间
    var month: Int = thisMonth//默认为当前月份

    private val billDao: BillDao
    private var mBillLiveData: MediatorLiveData<List<Bill>>
    var billImagesLiveData: MediatorLiveData<List<Image>> = MediatorLiveData()
    var compositeDisposable: CompositeDisposable? = CompositeDisposable()

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
            compositeDisposable!!.clear()
            if (mBillLiveData == null) mBillLiveData = MediatorLiveData()
            val start = TimeUtils.string2Millis(MyTimeUtils.getFirstDayOfMonth(year, month))
            LogUtils.d("Start time: ", start)
            val end = TimeUtils.string2Millis(MyTimeUtils.getLastDayOfMonth(year, month))
            LogUtils.d("End time: ", end)
            val disposable = billDao.findBillsFlowableByTime(start, end)
                    .subscribeOn(Schedulers.io()).distinctUntilChanged()
                    .map { bills: List<Bill> ->
                        LogUtils.i("input size " + bills.size)
                        val outs = bills.stream().filter { bill: Bill ->
                            val billTime = bill.billTime
                            val startTime = TimeUtils.string2Millis(MyTimeUtils.getFirstDayOfMonth(year, month))
                            val stopTime = TimeUtils.string2Millis(MyTimeUtils.getLastDayOfMonth(year, month))
                            if (billTime >= startTime && billTime <= stopTime) {
                                return@filter true
                            }
                            false
                        }.collect(Collectors.toList())
                        LogUtils.i("output size " + outs.size)
                        outs
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { bills: List<Bill>? -> mBillLiveData!!.postValue(bills) }
            compositeDisposable!!.add(disposable)
            return Transformations.distinctUntilChanged(mBillLiveData!!)
        }

    fun getBillImages(billId: String): MediatorLiveData<List<Image>> {
        launchIO({
            billImagesLiveData.postValue(AppDatabase.getInstance().imageDao().findByBillId(billId))
        }, {
            it.printStackTrace()
        })
        return billImagesLiveData
    }

    override fun onCleared() {
        super.onCleared()
        if (null != compositeDisposable) {
            if (!compositeDisposable!!.isDisposed) {
                compositeDisposable!!.clear()
                compositeDisposable!!.dispose()
            }
        }
    }

    fun getIncomesOrExpenses(year: Int, month: Int, type: Int): LiveData<Double> {
        val start = TimeUtils.string2Millis(MyTimeUtils.getFirstDayOfMonth(year, month))
        LogUtils.d("Start time: ", start)
        val end = TimeUtils.string2Millis(MyTimeUtils.getLastDayOfMonth(year, month))
        LogUtils.d("End time: ", end)
        return Transformations.distinctUntilChanged(AppDatabase.getInstance().billDao().findTotalMoneyByTime(start, end, type))
    }

    //return TimeUtils.getNowDate().getYear();
    private val thisYear: Int
        private get() = Calendar.getInstance()[Calendar.YEAR]
    //return TimeUtils.getNowDate().getYear();

    //return TimeUtils.getNowDate().getMonth();
    private val thisMonth: Int
        private get() = Calendar.getInstance()[Calendar.MONTH] + 1
    //return TimeUtils.getNowDate().getMonth();

}