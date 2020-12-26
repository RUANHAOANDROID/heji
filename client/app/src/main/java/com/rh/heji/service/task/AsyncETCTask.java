package com.rh.heji.service.task;

import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.data.network.ETCServer;
import com.rh.heji.utlis.http.basic.ServiceCreator;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Date: 2020/10/27
 * Author: 锅得铁
 * #
 */
public class AsyncETCTask implements Runnable {
    ETCServer etcServer = (ETCServer) ServiceCreator.getInstance().createService(ETCServer.class, ETCServer.ETC_BASE_URL);
    public static final String ETC_ID = "42021909230571219224";
    public static final String CAR_NUMBER = "鄂FNA518";
    public static final String YY_MM = "202010";

    @Override
    public void run() {
        try {
            Response<ResponseBody> response = etcServer.queryETCByMonth(ETC_ID, CAR_NUMBER, YY_MM).execute();
            if (null != response && response.isSuccessful()){
                if (response.code() ==200){
                    LogUtils.e(response.body().string());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
