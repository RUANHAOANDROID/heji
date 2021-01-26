package com.rh.heji.service.task;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.App;
import com.rh.heji.AppCache;
import com.rh.heji.Constants;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Image;
import com.rh.heji.data.network.BaseResponse;
import com.rh.heji.data.network.HeJiServer;
import com.rh.heji.data.network.request.BillEntity;
import com.rh.heji.utlis.http.basic.ServiceCreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import top.zibin.luban.Luban;

/**
 * Date: 2020/10/29
 * Author: 锅得铁
 * #
 */
public class AsyncPushTask implements Runnable {

    private HeJiServer heJiServer = AppCache.getInstance().getHeJiServer();

    public AsyncPushTask(List<String> bills) {
        this.bills = bills;
    }

    private List<String> bills;

    @Override
    public void run() {
        try {
            if (null != bills && bills.size() > 0) {
                uploadBills(bills);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadBills(List<String> billIds) throws IOException {
        billIds.stream().forEach(uid -> {
            List<Bill> bills = AppDatabase.getInstance().billDao().findBillByIdAndSyncStatus(uid, Bill.STATUS_NOT_SYNC);
            if (bills.size() > 0)
                bills.forEach(bill -> {
                    Response<BaseResponse<String>> response = null;
                    try {
                        response = heJiServer.saveBill(new BillEntity(bill)).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null != response && response.isSuccessful()) {
                        if (response.code() == 200) {
                            BaseResponse entity = response.body();
                            bill.setId(String.valueOf(entity.getDate()));
                            bill.setSynced(Bill.STATUS_SYNCED);
                            int updateCount =AppDatabase.getInstance().billDao().update(bill);
                            LogUtils.d(entity.toString());
                            if (bill.getImgCount() > 0) {
                                uploadImage(bill);
                            }
                        }
                    }
                });
        });
    }


    private void uploadImage(Bill bill) {
        List<Image> images = AppDatabase.getInstance().imageDao().findByBillImgIdNotAsync(bill.getId());
        if (null != images) {
            if (images.size() <= 0) {
                return;
            }
        }
        LogUtils.d("image size", images.size());
        //上传图片文件
        images.forEach(image -> {
            File img = new File(image.getLocalPath());
            try {
                long length =img.length();
                LogUtils.i("图片大小",length);
                if (length>Constants.FILE_LENGTH_1M *1){//图片超过设定值则压缩
                    LogUtils.i("图片大小超过1M,压缩图片",Constants.FILE_LENGTH_1M *1);
                    List<File> fileList = Luban.with(App.getContext()).load(img).get();
                    if (!fileList.isEmpty() && fileList.size() > 0) {
                        img = fileList.get(0);
                    }
                }
                String fileName =img.getName();
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), img);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", fileName, requestBody);
                long time = img.lastModified();
                Response<BaseResponse<String>> ticketResponse = heJiServer.uploadImg(part, bill.getId(),time).execute();
                if (null != ticketResponse && ticketResponse.isSuccessful()) {
                    if (ticketResponse.code() == 200) {
                        String ticketsUUID = ticketResponse.body().data;
                        image.setOnlinePath(ticketsUUID);
                        image.setSynced(Bill.STATUS_SYNCED);
                        AppDatabase.getInstance().billDao().update(bill);
                        AppDatabase.getInstance().imageDao().update(image);
                        LogUtils.d("图片上传成功：",ticketsUUID);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    private void uploadImages(List<String> mSelected) throws IOException {
        HeJiServer service = (HeJiServer) ServiceCreator.getInstance().createService(HeJiServer.class);
        //MultipartBody.Builder builder = new MultipartBody.Builder();
        List<MultipartBody.Part> parts = new ArrayList<>();
        mSelected.forEach(s -> {
            File img = new File(s);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), img);
            //builder.addFormDataPart("file", img.getName(), requestBody);

            MultipartBody.Part body = MultipartBody.Part.createFormData("files", UUID.randomUUID().toString(), requestBody);
            parts.add(body);
            //Headers headers = new Headers.Builder().add("time", String.valueOf(img.lastModified())).build();
            //builder.setType(MultipartBody.FORM);
            //builder.addPart(Part.create(headers, requestBody));
        });
        //MultipartBody multipartBody = builder.build();

        service.uploadImgs(parts).execute();
    }
}
