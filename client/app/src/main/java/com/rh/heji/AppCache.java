package com.rh.heji;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rh.heji.network.HeJiServer;
import com.rh.heji.utlis.http.basic.ServiceCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
    AppViewModule appViewModule = null;

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

    public AppViewModule getAppViewModule() {
        if (appViewModule == null)
            appViewModule = new AppViewModule((Application) context);
        return appViewModule;
    }

    /**
     * 把文件公开。添加到扫描中
     *
     * @param currentPhotoPath
     */
    public void galleryAddPic(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public String getToken() {
        File tokenFile = new File(context.getFilesDir(), "TokenFile");
        String token = "";
        if (tokenFile.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(tokenFile);
                InputStreamReader inputStreamReader =
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line = reader.readLine();
                    while (line != null) {
                        stringBuilder.append(line);
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    // Error occurred when opening raw file for reading.
                    e.printStackTrace();
                } finally {
                    token = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new String(EncodeUtils.base64Decode(token));
    }

    public void saveToken(String token) {
        //token = EncodeUtils.base64Encode(token);
        String fileName = "TokenFile";
        File file = new File(context.getFilesDir().getAbsolutePath());
        File tokenFile = new File(file, fileName);
        FileUtils.createFileByDeleteOldFile(tokenFile);
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(EncodeUtils.base64Encode(token));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteToken() {
        String fileName = "TokenFile";
        File file = new File(context.getFilesDir().getAbsolutePath());
        File tokenFile = new File(file, fileName);
        FileUtils.delete(tokenFile);
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(getToken());
    }
}
