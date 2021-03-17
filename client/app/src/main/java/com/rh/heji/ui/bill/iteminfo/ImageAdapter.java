package com.rh.heji.ui.bill.iteminfo;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.R;
import com.rh.heji.data.db.Image;
import com.rh.heji.databinding.ItemImgBinding;
import com.rh.heji.utlis.GlideApp;

import org.jetbrains.annotations.NotNull;

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
