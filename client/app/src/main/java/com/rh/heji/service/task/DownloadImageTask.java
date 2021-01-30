package com.rh.heji.service.task;

import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.App;
import com.rh.heji.AppCache;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.BillDao;
import com.rh.heji.data.db.Constant;
import com.rh.heji.data.db.Image;
import com.rh.heji.data.db.ImageDao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

/**
 * Date: 2020/11/18
 * Author: 锅得铁
 * #
 */
public class DownloadImageTask implements Runnable {
    ImageDao imageDao = AppDatabase.getInstance().imageDao();
    List<Image> notDownloadImages;

    public DownloadImageTask(List<Image> notDownloadImages) {
        this.notDownloadImages = notDownloadImages;
    }

    @Override
    public void run() {
        loadTicketImgFile(notDownloadImages);
    }

    private void loadTicketImgFile(List<Image> tickets) {
        if (!tickets.isEmpty() && tickets.size() > 0) {
            tickets.forEach(img -> {
                String localPath = img.getLocalPath();
                if (TextUtils.isEmpty(localPath)) {
                    String serverPath = img.getOnlinePath();
                    if (!TextUtils.isEmpty(serverPath)) {
                        try {
                            Response<ResponseBody> response = AppCache.getInstance().getHeJiServer().getImage(serverPath).execute();
                            if (null != response && response.isSuccessful() && response.code() == 200) {
                                final File filesDir = App.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                File imgFile = new File(filesDir, img.getOnlinePath());
                                try {
                                    BufferedSink sink = Okio.buffer(Okio.sink(imgFile));
                                    sink.writeAll(response.body().source());
                                    sink.flush();
                                    sink.close();
                                    img.setLocalPath(imgFile.getAbsolutePath());
                                    int updateCount =imageDao.updateImageLocalPath(String.valueOf(img.getId()), imgFile.getAbsolutePath(), Constant.STATUS_SYNCED);
                                    LogUtils.d("已更新本地照片：", updateCount,img.getId(), imgFile.getAbsolutePath());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        }
    }

}
