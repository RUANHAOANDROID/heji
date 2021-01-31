package com.rh.heji;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelStore;

import com.balsikandar.crashreporter.CrashReporter;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.Dealer;
import com.rh.heji.utlis.http.basic.HttpRetrofit;
import com.rh.heji.utlis.http.basic.OkHttpConfig;

import java.io.File;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
public class App extends Application {
    static Context context;
    private ViewModelStore appViewModelStore;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        appViewModelStore = new ViewModelStore();
        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            CrashUtils.init(storage("Crash"));
        }
        HttpRetrofit.initClient(OkHttpConfig.getClientBuilder().build());
        AppCache.init(this);
        AppCache.Companion.getInstance().getAppViewModule();
        LogUtils.getConfig().setGlobalTag("tag");
        String nullString =null;
        Log.d(null,null);
    }

    @Override
    public void onTerminate() {
        if (BuildConfig.DEBUG) {
        }
        super.onTerminate();
    }
    public static Context getContext() {
        return context;
    }

    public ViewModelStore viewModelStore() {
        return appViewModelStore;
    }

    /**
     * com.rh.heji 持久文件存储目录
     *
     * @param path
     * @return
     */
    public static String storage(String path) {
        File headDir = getContext().getExternalFilesDir(path);
        if (!headDir.exists())
            headDir.mkdir();
        return headDir.getPath();
    }

}
