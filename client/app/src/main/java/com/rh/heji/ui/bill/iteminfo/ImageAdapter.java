package com.rh.heji.ui.bill.iteminfo;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
import com.lxj.xpopup.util.SmartGlideImageLoader;
import com.rh.heji.GlideApp;
import com.rh.heji.R;
import com.rh.heji.data.db.Image;
import com.rh.heji.databinding.ItemImgBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        binding.itemImage.setOnClickListener(v -> showGallery(holder.getLayoutPosition()));
        LogUtils.d(image.getLocalPath(), image.getOnlinePath(), path);
    }

    private void showGallery(int startPosition) {
        OnSrcViewUpdateListener srcViewUpdateListener = (popupView, position) -> popupView.updateSrcView((ImageView) getRecyclerView().getChildAt(position));
        new XPopup.Builder(getContext())
                .asImageViewer((ImageView) getRecyclerView().getChildAt(startPosition), startPosition, getPaths(),
                        false, false, -1, -1, -1, false,
                        Color.rgb(32, 36, 46),
                        srcViewUpdateListener, new SmartGlideImageLoader(R.mipmap.ic_launcher), null)
                .show();
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

    private List<Object> getPaths() {
        return getData().stream().map((Function<Image, Object>) image -> getImagePath(image)).collect(Collectors.toList());
    }
}
