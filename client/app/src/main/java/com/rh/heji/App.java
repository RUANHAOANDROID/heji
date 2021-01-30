package com.rh.heji;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.ViewModelStore;

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
        HttpRetrofit.initClient(OkHttpConfig.getClientBuilder().build());
        AppCache.init(this);
        AppCache.getInstance().getAppViewModule();
        CrashUtils.init();
        LogUtils.getConfig().setGlobalTag("tag");
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

    public static final String PATH_CARSH = "carsh";

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
