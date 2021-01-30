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
        fakeData();
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

    void fakeData() {
        Dealer u1 = new Dealer("司机");
        Dealer u2 = new Dealer("祝");
        Dealer u3 = new Dealer("皓");
        Dealer u4 = new Dealer("孔");
//        Category t1 = new Category("购车", 1, -1);
//        Category t2 = new Category("加气", 1, -1);
//        Category t3 = new Category("过路费", 1, -1);
//        Category t4 = new Category("配件", 1, -1);
//        Category t5 = new Category("维修", 1, -1);
//        Category t6 = new Category("装饰", 1, -1);
//        Category t7 = new Category("住宿", 1, -1);
//        Category t8 = new Category("吃饭", 1, -1);
//        Category t9 = new Category("罚款 ", 1, -1);
//        Category t10 = new Category("拉货 ", 1, 1);
        AppDatabase.getInstance().dealerDao().insert(u1);
        AppDatabase.getInstance().dealerDao().insert(u2);
        AppDatabase.getInstance().dealerDao().insert(u3);
        AppDatabase.getInstance().dealerDao().insert(u4);
//        AppDatabase.getInstance().categoryDao().insert(t1);
//        AppDatabase.getInstance().categoryDao().insert(t2);
//        AppDatabase.getInstance().categoryDao().insert(t3);
//        AppDatabase.getInstance().categoryDao().insert(t4);
//        AppDatabase.getInstance().categoryDao().insert(t5);
//        AppDatabase.getInstance().categoryDao().insert(t6);
//        AppDatabase.getInstance().categoryDao().insert(t7);
//        AppDatabase.getInstance().categoryDao().insert(t8);
//        AppDatabase.getInstance().categoryDao().insert(t9);
//        AppDatabase.getInstance().categoryDao().insert(t10);
    }
}
