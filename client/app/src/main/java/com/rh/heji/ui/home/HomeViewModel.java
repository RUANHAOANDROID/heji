package com.rh.heji.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.BillDao;
import com.rh.heji.data.db.Image;
import com.rh.heji.utlis.MyTimeUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {
    private final BillDao billDao;
    int year;
    int month;
    private MediatorLiveData<List<Bill>> mBillLiveData;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public HomeViewModel() {
        mBillLiveData = new MediatorLiveData();
        billDao = AppDatabase.getInstance().billDao();
    }

    /**
     * 调用getBills之前必须先设定year ,month
     *
     * @return 账单列表
     */
    public LiveData<List<Bill>> getBills() {
        compositeDisposable.clear();
        if (mBillLiveData == null)
            mBillLiveData = new MediatorLiveData<>();
        long start = TimeUtils.string2Millis(MyTimeUtils.getFirstDayOfMonth(getYear(), getMonth()));
        LogUtils.d("Start time: ", start);
        long end = TimeUtils.string2Millis(MyTimeUtils.getLastDayOfMonth(getYear(), getMonth()));
        LogUtils.d("End time: ", end);

        Disposable disposable = billDao.findBillsFlowableByTime(start, end)
                .subscribeOn(Schedulers.io()).distinctUntilChanged()
                .map(bills -> {
                    LogUtils.i("input size " + bills.size());
                    List<Bill> outs = bills.stream().filter(bill -> {
                        long billTime = bill.getBillTime();
                        long startTime = TimeUtils.string2Millis(MyTimeUtils.getFirstDayOfMonth(getYear(), getMonth()));
                        long stopTime = TimeUtils.string2Millis(MyTimeUtils.getLastDayOfMonth(getYear(), getMonth()));
                        if (billTime >= startTime && billTime <= stopTime) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
                    LogUtils.i("output size " + outs.size());
                    return outs;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bills -> mBillLiveData.postValue(bills));
        compositeDisposable.add(disposable);
        return Transformations.distinctUntilChanged(mBillLiveData);
    }

    public List<Image> getBillImages(String billId) {
        return AppDatabase.getInstance().imageDao().findByBillImgId(billId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != compositeDisposable) {
            if (!compositeDisposable.isDisposed()) {
                compositeDisposable.clear();
                compositeDisposable.dispose();
            }
        }
    }

    public LiveData<Double> getIncomesOrExpenses(int year, int month, int type) {
        long start = TimeUtils.string2Millis(MyTimeUtils.getFirstDayOfMonth(getYear(), getMonth()));
        LogUtils.d("Start time: ", start);
        long end = TimeUtils.string2Millis(MyTimeUtils.getLastDayOfMonth(getYear(), getMonth()));
        LogUtils.d("End time: ", end);
        return Transformations.distinctUntilChanged(AppDatabase.getInstance().billDao().findTotalMoneyByTime(start, end, type));
    }

    public int getYear() {
        if (year == 0)
            return getThisYear();
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        if (month == 0)
            return getThisMonth();
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    private int getThisYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
        //return TimeUtils.getNowDate().getYear();
    }

    private int getThisMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
        //return TimeUtils.getNowDate().getMonth();
    }

}