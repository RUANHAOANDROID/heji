package com.rh.heji.ui.bill.img;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #
 */
public class ImageLoader implements XPopupImageLoader {
    @Override
    public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
        Glide.with(imageView).load(url).into(imageView);
        LogUtils.d(url);
    }


    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
