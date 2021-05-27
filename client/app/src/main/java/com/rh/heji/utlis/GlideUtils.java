package com.rh.heji.utlis;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.rh.heji.App;
import com.rh.heji.AppCache;
import com.rh.heji.utlis.http.basic.OkHttpConfig;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

@GlideModule
public class GlideUtils extends AppGlideModule {

    /**
     * @param pathName getExternalCacheDir() + "/image.jpg"
     */
    public static void loadImageFile(Context context, String pathName, ImageView imgView) {
        // 加载本地图片
        File file = new File(pathName);
        Glide.with(AppCache.Companion.getInstance().getContext()).load(file).into(imgView);
    }

    public static void loadImageRes(Context context, int resource, ImageView imgView) {
        // 加载应用资源
        Glide.with(context).load(resource).into(imgView);
    }

    public static void loadImageBytes(Context context, byte[] image, ImageView imgView) {
        // 加载二进制流
        Glide.with(context).load(image).into(imgView);
    }

    public static void loadImageUri(Context context, Uri imageUri, ImageView imgView) {
        // 加载Uri对象
        Glide.with(context).load(imageUri).into(imgView);
    }

    public static void loadImageUrl(Context context, String imageUri, ImageView imgView) {
        // 加载Uri对象
        Glide.with(context).load(imageUri).into(imgView);
    }
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        final OkHttpClient.Builder builder = OkHttpConfig.getClientBuilder();
        registry.append(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(builder.build()));
    }

}
