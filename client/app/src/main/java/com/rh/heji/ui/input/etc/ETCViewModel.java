package com.rh.heji.ui.input.etc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.mongo.ObjectId;
import com.rh.heji.utlis.http.basic.OkHttpConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Date: 2020/11/10
 * Author: 锅得铁
 * #
 */
public class ETCViewModel extends ViewModel {
    public static final String ETC_URL = "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcsearch";
    public static final String DEF_ETC_ID = "42021909230571219224";
    public static final String DEF_CAR_ID = "鄂FNA518";
    //伪装User-Agent
    public static final String[] USER_AGENTS = {
            //"Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0",
            "Mozilla/5.0 (Linux; Android 10; MI 8 Lite Build/QKQ1.190910.002; wv) ",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36",
    };
    String etcID=DEF_ETC_ID;
    String carID=DEF_CAR_ID;
    String yearMonth;
    MediatorLiveData<String> etcLive = new MediatorLiveData();

    /**
     * 获取类别
     *
     * @return 类别名称
     */
    private String getCategoryName() {
        List<Category> categories = AppDatabase.getInstance().categoryDao().queryByCategoryName("过路费");
        Category category;
        if (categories.size() <= 0) {
            category = new Category("过路费", 1, BillType.EXPENDITURE.type());
            category.setSynced(Category.STATUS_NOT_SYNC);
            AppDatabase.getInstance().categoryDao().insert(category);
        } else {
            category = categories.get(0);
        }
        return category.getCategory();
    }

    /**
     * 保存到数据库
     *
     * @param strBody 内容
     * @return
     */
    private void saveToDB(String strBody) {
        Gson gson = new Gson();
        ETCListInfoEntity etcListInfo = gson.fromJson(strBody, ETCListInfoEntity.class);
        if (etcListInfo != null && etcListInfo.data != null && etcListInfo.data.size() > 0) {
            List<ETCListInfoEntity.Info> data = etcListInfo.data;
            data.forEach(info -> {
                Bill bill = new Bill();
                bill.setId(new ObjectId().toString());
                bill.setMoney(new BigDecimal(info.etcPrice).divide(new BigDecimal(100)));
                bill.setRemark(info.exEnStationName);
                bill.setBillTime(TimeUtils.string2Millis(info.exchargetime, "yyyy-MM-dd HH:mm:ss"));
                bill.setCategory(getCategoryName());
                bill.setDealer("ETC");
                bill.setCreateTime(TimeUtils.getNowMills());
                bill.setType(BillType.EXPENDITURE.type());
                /**
                 * 如果不存在才插入
                 */
                List<String> bills = AppDatabase.getInstance().billDao().findBill(bill.getBillTime(), bill.getMoney(), bill.getRemark());
                if (bills.size() <= 0) {
                    AppDatabase.getInstance().billDao().install(bill);
                    LogUtils.d("导入ETC账单：", bill);
                } else {
                    LogUtils.d("ETC账单已存在", bills);
                }

            });
            etcLive.postValue("导入完成");
        } else {
            ToastUtils.showShort("导入失败");
            etcLive.postValue("导入失败");
        }

    }

    /**
     * 请求账单详情列表
     *
     * @param etcID ETC号码
     * @param month 月份
     * @param carID 车牌号
     */
    public LiveData<String> requestETCList2(String etcID, String month, String carID) {
        //伪装User-Agent
        String url = "http://hubeiweixin.u-road.com:80/HuBeiCityAPIServer/index.php/huibeicityserver/loadmonthinfo";
        //www - url 解码方式
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/x-www-form-urlencoded"),
                        "caidno=" + etcID + "&month=" + month + "&vehplate=" + carID);
        //伪装成浏览器请求
        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .addHeader("User-Agent", USER_AGENTS[new Random().nextInt(USER_AGENTS.length - 1)])
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .post(requestBody)
                .build();
        OkHttpConfig.getClientBuilder().build().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (null != response && response.isSuccessful()) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String strBody = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(strBody);
                                String status = jsonObject.getString("status");
                                if (status.equals("error")) {
                                    String error = jsonObject.getString("msg");
                                    ToastUtils.showLong(error);
                                } else {
                                    saveToDB(strBody);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.showShort("解析失败");
                                etcLive.postValue("解析错误");
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                ToastUtils.showLong(e.getMessage());
                etcLive.postValue("请求错误");
            }

        });
        return etcLive;
    }

    /**
     * 请求账单详情列表
     *
     * @param etcID ETC号码
     * @param month 月份
     * @param carID 车牌号
     */
    public LiveData<String> requestHBGSETCList(String etcID, String month, String carID) {

        String requestURL = "http://www.hbgsetc.com/index.php?/newhome/getMonthBillData";
        //www - url 解码方式
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/x-www-form-urlencoded"),
                        "cardNo=" + etcID + "&month=" + month + "&vehplate=" + carID+"&flag=0");
        //伪装成浏览器请求
        Request request = new Request.Builder()
                .url(requestURL)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .addHeader("User-Agent", USER_AGENTS[new Random().nextInt(USER_AGENTS.length - 1)])
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .post(requestBody)
                .build();
        OkHttpConfig.getClientBuilder().build().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (null != response && response.isSuccessful()) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String strBody = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(strBody);
                                String status = jsonObject.getString("status");
                                if (status.equals("error")) {
                                    String error = jsonObject.getString("msg");
                                    ToastUtils.showLong(error);
                                } else if (status.equals("OK")) {
                                    Gson gson =new Gson();
                                    HBETCEntity hbetcEntity =gson.fromJson(strBody,HBETCEntity.class);

                                    if (hbetcEntity != null && hbetcEntity.data != null && hbetcEntity.data.orderArr.size() > 0) {
                                        List<HBETCEntity.DataBean.OrderArrBean> data = hbetcEntity.data.orderArr;
                                        data.forEach(info -> {
                                            saveToBillDB(info);

                                        });
                                        etcLive.postValue("导入完成");
                                    } else {
                                        ToastUtils.showShort("导入失败");
                                        etcLive.postValue("导入失败");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.showShort("解析失败");
                                etcLive.postValue("解析错误");
                            }
                        }
                    }
                }else if (response.code() ==404){
                    requestETCList2(etcID,month,carID);
                }

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                ToastUtils.showLong(e.getMessage());
                etcLive.postValue("请求错误");
            }

        });
        return etcLive;
    }

    private void saveToBillDB(HBETCEntity.DataBean.OrderArrBean info) {
        int money = info.totalFee;
        String remark = info.enStationName + "|" + info.exStationName;
        long billTime = TimeUtils.string2Millis(info.exTime, "yyyy-MM-dd HH:mm:ss");

        Bill bill = new Bill();
        bill.setId(new ObjectId().toString());
        bill.setMoney(new BigDecimal(money).divide(new BigDecimal(100)));
        bill.setRemark(remark);
        bill.setBillTime(billTime);
        bill.setCategory(getCategoryName());
        bill.setDealer("ETC");
        bill.setCreateTime(TimeUtils.getNowMills());
        bill.setType(BillType.EXPENDITURE.type());
        /**
         * 如果不存在才插入(插入时必须保持格式一致)
         */
        List<String> bills = AppDatabase.getInstance().billDao().findBill(bill.getBillTime(), bill.getMoney(), bill.getRemark());
        if (bills.size() <= 0) {
            AppDatabase.getInstance().billDao().install(bill);
            LogUtils.d("导入ETC账单：", bill);
        } else {
            LogUtils.d("ETC账单已存在", bills);
        }
    }

    public String getEtcID() {
        return etcID;
    }

    public void setEtcID(String etcID) {
        this.etcID = etcID;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
}
