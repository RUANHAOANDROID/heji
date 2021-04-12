package com.rh.heji.utlis;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.rh.heji.App;

import java.io.File;
@GlideModule
public class GlideUtils extends AppGlideModule {

    /**
     * @param pathName getExternalCacheDir() + "/image.jpg"
     */
    public static void loadImageFile(Context context, String pathName, ImageView imgView) {
        // 加载本地图片
        File file = new File(pathName);
        Glide.with(App.getContext()).load(file).into(imgView);
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
}
