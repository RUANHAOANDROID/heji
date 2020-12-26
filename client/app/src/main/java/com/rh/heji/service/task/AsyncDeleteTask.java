package com.rh.heji.service.task;

import com.rh.heji.AppCache;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.ImageDao;
import com.rh.heji.data.network.BaseResponse;
import com.rh.heji.data.network.HeJiServer;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

/**
 * Date: 2020/11/5
 * Author: 锅得铁
 * #
 */
public class AsyncDeleteTask implements Runnable {
    ImageDao imageDao = AppDatabase.getInstance().imageDao();
    List<String> deleteBills;
    private HeJiServer heJiServer = AppCache.getInstance().getHeJiServer();

    public AsyncDeleteTask(List<String> deleteBills) {
        this.deleteBills = deleteBills;
    }

    @Override
    public void run() {
        if (null != deleteBills && deleteBills.size() > 0) {
            try {
                deleteAsyncBills(deleteBills);
                deleteTicketImage(deleteBills);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除账单图片票据
     *
     * @param ids
     */
    private void deleteTicketImage(List<String> ids) {
        if (null != ids && ids.size() > 0) {
            ids.forEach(id -> {
                imageDao.deleteById(id);
            });
        }

    }

    /**
     * 删除服务器上的账单
     * ids 删除的IDs
     *
     * @throws IOException
     */
    private void deleteAsyncBills(List<String> ids) throws IOException {
        ids.forEach(id -> {
            try {
                Response<BaseResponse> response = heJiServer.deleteBill(id).execute();
                if (null != response && response.isSuccessful()) {
                    if (response.code() == 200) {
                        if(response.body().code==0||response.body().msg.contains("不存在")){
                            Bill bill = new Bill();
                            bill.setId(id);
                            AppDatabase.getInstance().billDao().delete(bill);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
