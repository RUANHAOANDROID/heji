package com.rh.heji.utlis;

import android.text.TextUtils;

import com.rh.heji.data.db.Image;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Date: 2022/3/29
 * Author: 锅得铁
 * #
 */
public class ImageUtils {
    /**
     * 获取图片路径，优先获取本地图片路径
     *
     * @param image
     * @return
     */
    public static String getImagePath(Image image) {
        String path = "";
        boolean isLocalFileExists = !TextUtils.isEmpty(image.getLocalPath());
        if (isLocalFileExists) {
            path = image.getLocalPath();
        } else {
            path = image.getOnlinePath();
        }
        return path;
    }

    public static  List<Object> getPaths(List<Image> images) {
        return images.stream().map((Function<Image, Object>) image -> getImagePath(image)).collect(Collectors.toList());
    }
}
