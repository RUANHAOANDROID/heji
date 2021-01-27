package com.rh.heji.service.task;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.AppCache;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.Image;
import com.rh.heji.network.BaseResponse;
import com.rh.heji.network.HeJiServer;
import com.rh.heji.network.request.BillEntity;
import com.rh.heji.network.request.CategoryEntity;
import com.rh.heji.utlis.MyTimeUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Response;

/**
 * Date: 2020/11/5
 * Author: 锅得铁
 * #
 */
public class AsyncPullTask implements Runnable {
    private HeJiServer heJiServer = AppCache.getInstance().getHeJiServer();

    @Override
    public void run() {

        try {
            getCategory();
            pullBills();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拉服务器上的账单
     */
    private void pullBills() throws IOException {
        Calendar calendar = Calendar.getInstance();
        String startTime = MyTimeUtils.getFirstDayOfMonth(2020, 8);
        String endTime = MyTimeUtils.getFirstDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        Response<BaseResponse<List<BillEntity>>> response = heJiServer.getBills("0", "0").execute();
        if (null != response && response.isSuccessful()) {
            if (response.code() == 200) {
                List<BillEntity> data = response.body().data;
                if (null==data)return ;
                if (data.size()<=0)return;
                data.forEach(responseBill -> {
                    List<Bill> bills = AppDatabase.getInstance().billDao().findByBillId(responseBill.getId());
                    Bill bill = null;
                    if (bills.size() > 0) {
                        bill = bills.get(0);
                    }
                    if (null == bill) {
                        bill = responseBill.toBill();
                        AppDatabase.getInstance().billDao().install(bill);
                    }
                    if (null != responseBill.getImages()) {
                        if (responseBill.getImages().size() > 0) {
                            String billId = bill.getId();

                            List<Image> images = responseBill.getImages().stream().map(serverID -> {
                                List<Image> localImage = AppDatabase.getInstance().imageDao().findByOnLinePath(serverID);
                                Image image =null;
                                if (!localImage.isEmpty() && localImage.size() > 0) {
                                    image = localImage.stream().findFirst().get();
                                }else {
                                    image = new Image(billId);
                                    image.setOnlinePath(serverID);
                                    AppDatabase.getInstance().imageDao().install(image);
                                }
                                return image;
                            }).collect(Collectors.toList());
                            AppDatabase.getInstance().billDao().updateImageCount(images.size(), billId);
                        }
                    }

//                    try {
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                });

            }
        }
    }


    private void getCategory() throws IOException {
        Response<BaseResponse<List<CategoryEntity>>> response = heJiServer.getCategories("0").execute();
        if (null != response && response.isSuccessful()) {
            if (response.code() == 200) {
                List<CategoryEntity> categories = response.body().data;
                if (categories != null && categories.size() > 0) {
                    LogUtils.i(categories);
                    categories.stream().forEach(entity -> {
                        String localCategoryName = AppDatabase.getInstance().categoryDao().existsByName(entity.getName());
                        if (TextUtils.isEmpty(localCategoryName)) {
                            Category dbCategory = entity.toDbCategory();
                            dbCategory.setSynced(Category.STATUS_SYNCED);
                            AppDatabase.getInstance().categoryDao().insert(dbCategory);
                        }
                    });

                }
            }
        }

    }

}
