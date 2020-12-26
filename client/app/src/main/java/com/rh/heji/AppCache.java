package com.rh.heji;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rh.heji.data.network.HeJiServer;
import com.rh.heji.utlis.http.basic.ServiceCreator;

import java.io.File;

/**
 * Date: 2020/11/18
 * Author: 锅得铁
 * #与APP同在
 */
public final class AppCache {
    private static final AppCache appCache = new AppCache();
    Context context;
    private HeJiServer service;
    private Gson gson = new GsonBuilder().create();

    public static AppCache getInstance() {
        return appCache;
    }

    public static void init(Application app) {
        getInstance().onInit(app);
    }

    public void onInit(Application app) {
        context = app;
        service = (HeJiServer) ServiceCreator.getInstance().createService(HeJiServer.class);
    }

    public HeJiServer getHeJiServer() {
        return service;
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * 把文件公开。添加到扫描中
     * @param currentPhotoPath
     */
    public void galleryAddPic(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
