package com.rh.heji.ui.bill.Iteminfo;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.App;
import com.rh.heji.R;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Constant;
import com.rh.heji.data.db.Image;
import com.rh.heji.databinding.ItemImgBinding;
import com.rh.heji.utlis.GlideApp;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 票据图片Adapter
 */
public class ImageAdapter extends BaseQuickAdapter<Image, BaseViewHolder> {
    ItemImgBinding binding;

    public ImageAdapter() {
        super(R.layout.item_img);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Image image) {
        binding = ItemImgBinding.bind(holder.itemView);
        String path = getImagePath(image);
        GlideApp.with(binding.itemImage)
                .asBitmap()
                .load(path)
                .error(R.drawable.ic_baseline_image_load_error_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(binding.itemImage);
        LogUtils.i(image.getLocalPath(), image.getOnlinePath(), path);
    }

    private void saveBitmap(Bitmap resource, Image image) {
        final File filesDir = App.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);//APP图片目录
        String[] strings = image.getOnlinePath().split("/");//因为之前添加了url前缀，切割online path
        File imgFile = new File(filesDir, strings[strings.length - 1]);
        if (!FileUtils.isFileExists(imgFile))//文件不存在
            ImageUtils.save(resource, imgFile, Bitmap.CompressFormat.JPEG);
        if (FileUtils.isFileExists(imgFile)) {
            String imageLocalPath = imgFile.getAbsolutePath();
            AppDatabase.getInstance().imageDao().updateImageLocalPath(String.valueOf(image.getId()), imageLocalPath, Constant.STATUS_SYNCED);
        }
    }

    /**
     * 获取图片路径，优先获取本地图片路径
     *
     * @param image
     * @return
     */
    public String getImagePath(Image image) {
        String path = "";
        boolean isLocalFileExists = !TextUtils.isEmpty(image.getLocalPath());
        if (isLocalFileExists) {
            path = image.getLocalPath();
        } else {
            path = image.getOnlinePath();
        }
        return path;
    }

}
