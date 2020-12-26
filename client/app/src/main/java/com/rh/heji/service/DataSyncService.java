package com.rh.heji.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.work.impl.utils.LiveDataUtils;
import androidx.work.impl.utils.SerialExecutor;
import androidx.work.impl.utils.taskexecutor.TaskExecutor;

import com.blankj.utilcode.util.BusUtils;
import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.MainActivity;
import com.rh.heji.R;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.Image;
import com.rh.heji.service.task.AsyncDeleteTask;
import com.rh.heji.service.task.AsyncPullTask;
import com.rh.heji.service.task.AsyncPushTask;
import com.rh.heji.service.task.AsyncCategoryTask;
import com.rh.heji.service.task.DownloadImageTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

/**
 * Date: 2020/9/23
 * Author: 锅得铁
 * #
 */
public class DataSyncService extends Service {

    private Notification notification;
    private PendingIntent pendingIntent;
    public static final String CHANNEL_ID = "location_notification_channel_id";
    public static final String CHANNEL_NAME = "Location Notification Service";
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 100;
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    private ExecutorService mExecutor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutor = Executors.newCachedThreadPool();
        BusUtils.register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setNotification();
        if (intent != null) {//
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //create foreground service
                        startForeground(NOTIFICATION_ID, notification);
                    } else {
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }

                    break;

                case ACTION_STOP_FOREGROUND_SERVICE:
                    shutdown();
                    stopService(intent);
                    break;
            }
        } else {
            return this.START_REDELIVER_INTENT;
        }


//        mExecutor.execute(new  AsyncETCTask());
        syncBills();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 观察未上传的
     */
    Observer<List<String>> notUploadObserver = billIds -> {
        if (null != billIds && billIds.size() > 0) {
            mExecutor.execute(new AsyncPushTask(billIds));
        }

    };
    /**
     * 观察已删除的
     */
    Observer<List<String>> deleteObserver = billIds -> {
        if (null != billIds && billIds.size() > 0)
            mExecutor.execute(new AsyncDeleteTask(billIds));
    };
    /**
     * 观察分类标签（数据量少，直接观察所有）
     */

    Observer<List<Category>> categoriesObserver = categories -> {
        if (null != categories && categories.size() > 0)
            mExecutor.execute(new AsyncCategoryTask(categories));
    };
    Observer<List<Image>> imagesObserver = new Observer<List<Image>>() {
        @Override
        public void onChanged(List<Image> images) {
            mExecutor.execute(new DownloadImageTask(images));
        }
    };
    LiveData<List<String>> deleteLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().billDao().observeSyncStatus(Bill.STATUS_DELETE));
    LiveData<List<String>> notUploadLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().billDao().observeSyncStatus(Bill.STATUS_NOT_SYNC));
    LiveData<List<Category>> categoryLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().categoryDao().observeNotUploadOrDelete());
    LiveData<List<Image>> imagesLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().imageDao().observerNotDownloadImages());

    @SuppressLint("RestrictedApi")
    private void syncBills() {
        /**
         * 先拉取数据本地没有则存入
         */
        mExecutor.execute(new AsyncPullTask());
        /**
         * 观察未上传的
         */
        notUploadLiveData.observeForever(notUploadObserver);
        /**
         * 观察已删除的
         */
        deleteLiveData.observeForever(deleteObserver);
        /**
         * 观察分类标签（数据量少，直接观察所有）
         */
        categoryLiveData.observeForever(categoriesObserver);
        /**
         * 图片下载任务
         */

        //imagesLiveData.observeForever(imagesObserver);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deleteLiveData != null) {
            if (deleteLiveData.hasObservers()) {
                deleteLiveData.removeObserver(deleteObserver);
            }
        }
        if (notUploadLiveData != null) {
            if (notUploadLiveData.hasObservers()) {
                notUploadLiveData.removeObserver(deleteObserver);
            }
        }
        if (categoryLiveData != null) {
            if (categoryLiveData.hasObservers()) {
                categoryLiveData.removeObserver(categoriesObserver);
            }
        }
        if (imagesLiveData != null) {
            if (imagesLiveData.hasObservers()) {
                imagesLiveData.removeObserver(imagesObserver);
            }
        }
        if (mExecutor != null)
            mExecutor.shutdown();

    }

    private void setNotification() {
        //open main activity when clicked
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_image_red_24dp)
                    .setContentTitle(getString(R.string.app_name) + "正在同步数据")
                    .setContentText("正在同步本地账单")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + "正在同步数据")
                    //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N | Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_image_red_24dp)
                    .setContentTitle(getString(R.string.app_name) + "正在同步数据")
                    .setContentText("正在同步本地账单")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setColor(getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + "正在同步数据")
                    //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setImportance(NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_image_red_24dp)
                    .setContentTitle(getString(R.string.app_name) + "正在同步数据")
                    .setContentText("正在同步本地账单")
                    .setPriority(PRIORITY_MIN)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setColor(getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + "正在同步数据")
                    //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .build();
        }
    }

    public void shutdown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "location_notification_channel_id";
            notificationManager.deleteNotificationChannel(id);
        } else {
            NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        }

    }
    @BusUtils.Bus(tag = "TAG",sticky = false,threadMode = BusUtils.ThreadMode.MAIN)
    public  void Bus(Object object){
        LogUtils.d("BUS",object);
    }
}
