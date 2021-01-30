package com.rh.heji.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.BusUtils
import com.rh.heji.AppCache
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.service.work.DataSyncWork
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Date: 2020/9/23
 * Author: 锅得铁
 * #
 */
class DataSyncService : Service() {
    private var notification: Notification? = null
    private var pendingIntent: PendingIntent? = null
    private var notificationManager: NotificationManager? = null
    private var mExecutor: ExecutorService? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mExecutor = Executors.newCachedThreadPool()
        BusUtils.register(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        setNotification()
        if (intent != null) { //
            when (intent.action) {
                ACTION_START_FOREGROUND_SERVICE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //create foreground service
                    startForeground(NOTIFICATION_ID, notification)
                } else {
                    notificationManager!!.notify(NOTIFICATION_ID, notification)
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    shutdown()
                    stopService(intent)
                }
            }
        } else {
            return START_REDELIVER_INTENT
        }
        syncBills()
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("RestrictedApi")
    private fun syncBills() {
        if (!AppCache.instance.isLogin) return
        GlobalScope.launch {
            var dataAsyncWork = DataSyncWork()
            dataAsyncWork.asyncBills()
            dataAsyncWork.asyncCategory()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mExecutor != null) mExecutor!!.shutdown()
    }

    private fun setNotification() {
        //open main activity when clicked
        pendingIntent = PendingIntent.getActivity(applicationContext, 0,
                Intent(applicationContext, MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            val notificationBuilder = NotificationCompat.Builder(this)
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_image_red_24dp)
                    .setContentTitle(getString(R.string.app_name) + "正在同步数据")
                    .setContentText("正在同步本地账单")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + "正在同步数据") //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_image_red_24dp)
                    .setContentTitle(getString(R.string.app_name) + "正在同步数据")
                    .setContentText("正在同步本地账单")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setColor(getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + "正在同步数据") //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            channel.importance = NotificationManager.IMPORTANCE_LOW
            channel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            notificationManager!!.createNotificationChannel(channel)
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_image_red_24dp)
                    .setContentTitle(getString(R.string.app_name) + "正在同步数据")
                    .setContentText("正在同步本地账单")
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setColor(getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + "正在同步数据") //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .build()
        }
    }

    private fun shutdown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = "location_notification_channel_id"
            notificationManager.deleteNotificationChannel(id)
        } else {
            val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    companion object {
        const val CHANNEL_ID = "location_notification_channel_id"
        const val CHANNEL_NAME = "Location Notification Service"
        const val NOTIFICATION_ID = 100
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
    }
}