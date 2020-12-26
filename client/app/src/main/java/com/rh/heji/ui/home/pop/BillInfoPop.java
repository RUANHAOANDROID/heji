package com.rh.heji.ui.home.pop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.BitmapThumbnailImageViewTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.enums.PopupType;
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
import com.matisse.utils.BitmapUtils;
import com.rh.heji.App;
import com.rh.heji.BuildConfig;
import com.rh.heji.R;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Image;
import com.rh.heji.databinding.ItemImgBinding;
import com.rh.heji.databinding.PopBilliInfoBinding;
import com.rh.heji.ui.home.ImageLoader;
import com.rh.heji.ui.home.TicketImageViewerPopup;
import com.rh.heji.utlis.GlideApp;
import com.rh.heji.utlis.GlideUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #
 */
public class BillInfoPop extends BottomPopupView {
    private Bill bill;
    PopBilliInfoBinding binding;
    private TicketPopuInfoAdapter ticketAdapter;

    public BillInfoPop(@NonNull Context context) {
        super(context);
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
        binding.tvMonney.setText(bill.getMoney().toString());
        binding.tvType.setText(bill.getCategory());
        binding.tvRecordTime.setText(TimeUtils.millis2String(bill.getCreateTime()));
        binding.tvTicketTime.setText(TimeUtils.millis2String(bill.getBillTime()));
        binding.rePeople.setText(bill.getDealer());
    }

    public void setBillImages(List<Image> images) {
        if (images.isEmpty()) return;
        initTicketImg();
        //服务器返回的是图片的ID、需要加上前缀
        List<Image> imagePaths = images.stream().map(image -> {
            String path = BuildConfig.HTTP_URL + "/file/image/" + image.getOnlinePath();
            image.setOnlinePath(path);
            return image;
        }).collect(Collectors.toList());
        ticketAdapter.setNewInstance(imagePaths);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_billi_info;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        binding = PopBilliInfoBinding.bind(getPopupContentView().findViewById(R.id.billInfoCard));
        binding.tvDelete.setOnClickListener(v -> {
            deleteBill();
        });
        binding.tvUpdate.setOnClickListener(v -> {
        });

    }

    private void initTicketImg() {
        binding.ticketRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ticketAdapter = new TicketPopuInfoAdapter();
        binding.ticketRecycler.setAdapter(ticketAdapter);

        ticketAdapter.setDiffCallback(new DiffUtil.ItemCallback<Image>() {
            @Override
            public boolean areItemsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
                return oldItem.equals(newItem);
            }
        });
        ticketAdapter.setOnItemClickListener((adapter, view, position) -> {
            showTicketImg(view, position);
        });

    }


    private void showTicketImg(View itemView, int itemPosition) {
        List<Object> objects = ticketAdapter.getData().stream().map((Function<Image, Object>) image -> ticketAdapter.getImagePath(image)).collect(Collectors.toList());
        if (objects.isEmpty()) return;
        new XPopup.Builder(getContext()).asImageViewer((ImageView) itemView, itemPosition, objects,
                (popupView, position1) -> {
                    popupView.updateSrcView((ImageView) ticketAdapter.getViewByPosition(itemPosition, R.id.itemImage));
                }, new ImageLoader())
                .isShowSaveButton(false)
                .show();
    }

    /**
     * 删除该条账单
     */
    private void deleteBill() {
        new XPopup.Builder(getContext()).asConfirm("删除提示", "确认删除该条账单吗？",
                () -> {
                    bill.setSynced(Bill.STATUS_DELETE);
                    AppDatabase.getInstance().billDao().update(bill);
//                     HeJiServer heJiServer = (HeJiServer) ServiceCreator.getInstance().createService(HeJiServer.class);
//                     heJiServer.deleteBill(bill.getId()).enqueue(new Callback<BaseResponse>() {
//                         @Override
//                         public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                             LogUtils.e(response.body().msg);
//                             if (null != response && response.isSuccessful()) {
//                                 if (response.code() == 200) {
//                                     AppDatabase.getInstance().billDao().delete(bill);
//                                 }
//                             }
//
//                         }
//
//                         @Override
//                         public void onFailure(Call<BaseResponse> call, Throwable t) {
//                             t.printStackTrace();
//                         }
//                     });
                    BillInfoPop.this.dismiss();
                }).show();

    }

    /**
     * 票据图片Adapter
     */
    public static class TicketPopuInfoAdapter extends BaseQuickAdapter<Image, BaseViewHolder> {
        ItemImgBinding binding;

        public TicketPopuInfoAdapter() {
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
                AppDatabase.getInstance().imageDao().updateImageLocalPath(String.valueOf(image.getId()),imageLocalPath,Bill.STATUS_SYNCED);
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
}
