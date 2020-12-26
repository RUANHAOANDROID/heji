package com.rh.heji.ui.add;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.Dealer;
import com.rh.heji.data.db.Image;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
public class AddBillViewModel extends ViewModel {
    private List<String> imgUrls = new ArrayList<>();
    private MutableLiveData<List<String>> imgUrlsLive = new MutableLiveData<>();
    private BillType billType = BillType.EXPENDITURE;
    private Bill bill = new Bill();
    private String time;//时间

    MutableLiveData<Bill> saveLiveData;

    public AddBillViewModel() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public Bill getBill() {
        return bill;
    }

    /**
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    public LiveData<Bill> save(String billId, String money, BillType billType) {
        saveLiveData = new MutableLiveData();
        long billTime = TimeUtils.string2Millis(getTime(), "yyyy-MM-dd HH:mm");
        LogUtils.d(TimeUtils.millis2String(billTime, "yyyy-MM-dd HH:mm"));

        Bill bill = getBill();
        bill.setId(billId);
        bill.setMoney(new BigDecimal(money));
        bill.setCreateTime(System.currentTimeMillis());
        bill.setBillTime(billTime);
        List<Image> images =imgUrls.stream().map(s -> {
            Image image = new Image(billId);
            image.setLocalPath(s);
            return image;
        }).collect(Collectors.toList());
        bill.setImgCount(images.size());
        if (bill.getCategory() ==null){
            bill.setType(billType.type());
            bill.setCategory(billType.text());
        }
        AppDatabase.getInstance().imageDao().install(images);
        long count = AppDatabase.getInstance().billDao().install(bill);
        if (count > 0) {
            ToastUtils.showShort(count + ": 保存成功");
        }
        saveLiveData.postValue(bill);
        return saveLiveData;
    }


    public void addImgUrl(String imgUrl) {
        imgUrls.add(imgUrl);
        imgUrlsLive.postValue(imgUrls);
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
        imgUrlsLive.postValue(imgUrls);
    }

    public void removeImgUrl(String imgUrl) {
        if (imgUrls.size() > 0 && imgUrls.contains(imgUrl)) {
            imgUrls.remove(imgUrl);
        }

        imgUrlsLive.postValue(imgUrls);
    }

    public MutableLiveData<List<String>> getImgUrlsLive() {
        imgUrlsLive.postValue(imgUrls);
        return imgUrlsLive;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public List<String> getDealers() {
        List<Dealer> users = AppDatabase.getInstance().dealerDao().findAll();
        List<String> dealerNames = new ArrayList<>();
        users.forEach(dealer -> dealerNames.add(dealer.getUserName()));
        return dealerNames;
    }

    public String getTime() {
        if (TextUtils.isEmpty(time))
            time = TimeUtils.getNowString();
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}