package com.rh.heji.ui.home;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.ImageViewerPopupView;
import com.rh.heji.R;
import com.rh.heji.databinding.CustomImageViewerPopupBinding;

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #
 */
public class TicketImageViewerPopup extends ImageViewerPopupView {
    private boolean allowDelete = false;
    //CustomImageViewerPopupBinding binding;

    public TicketImageViewerPopup(@NonNull Context context) {
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

    public TicketImageViewerPopup setAllowDelete(boolean allowDelete) {
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