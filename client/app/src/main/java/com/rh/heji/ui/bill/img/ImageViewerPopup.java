package com.rh.heji.ui.bill.img;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.ImageViewerPopupView;
import com.rh.heji.R;

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #
 */
public class ImageViewerPopup extends ImageViewerPopupView {
    private boolean allowDelete = false;
    //CustomImageViewerPopupBinding binding;

    public ImageViewerPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_image_viewer_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
//        binding = CustomImageViewerPopupBinding.bind(getRootView());
//        binding.imgDelete.setVisibility(allowDelete ? VISIBLE : GONE);
//        tv_pager_indicator.setVisibility(GONE);
    }

    public boolean isAllowDelete() {
        return allowDelete;
    }

    public ImageViewerPopup setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}