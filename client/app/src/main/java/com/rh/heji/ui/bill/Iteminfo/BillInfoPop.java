package com.rh.heji.ui.bill.Iteminfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.rh.heji.App;
import com.rh.heji.AppCache;
import com.rh.heji.BuildConfig;
import com.rh.heji.R;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Constant;
import com.rh.heji.data.db.Image;
import com.rh.heji.databinding.ItemImgBinding;
import com.rh.heji.databinding.PopBilliInfoBinding;
import com.rh.heji.ui.bill.img.ImageLoader;
import com.rh.heji.ui.user.JWTParse;
import com.rh.heji.utlis.GlideApp;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #
 */
public class BillInfoPop extends BottomPopupView {
    private Bill bill;
    PopBilliInfoBinding binding;
    private ImagePopupInfoAdapter imagePopupInfoAdapter;

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
        if (images.isEmpty()) {
            if (imagePopupInfoAdapter != null) {
                imagePopupInfoAdapter.setNewInstance(new ArrayList<>());
                imagePopupInfoAdapter.notifyDataSetChanged();
            }
            return;
        }

        initTicketImg();
        //服务器返回的是图片的ID、需要加上前缀
        List<Image> imagePaths = images.stream().map(image -> {
            String onlinePath = image.getOnlinePath();
            if (null != onlinePath && !image.getOnlinePath().contains("http")) {
                String path = BuildConfig.HTTP_URL + "/image/" + image.getOnlinePath();
                image.setOnlinePath(path);
            }
            return image;
        }).collect(Collectors.toList());
        imagePopupInfoAdapter.setNewInstance(imagePaths);
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

        imagePopupInfoAdapter = new ImagePopupInfoAdapter();
        binding.ticketRecycler.setAdapter(imagePopupInfoAdapter);

        imagePopupInfoAdapter.setDiffCallback(new DiffUtil.ItemCallback<Image>() {
            @Override
            public boolean areItemsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
                return oldItem.equals(newItem);
            }
        });
        imagePopupInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
            showTicketImg(view, position);
        });

    }


    private void showTicketImg(View itemView, int itemPosition) {
        List<Object> objects = imagePopupInfoAdapter.getData().stream().map((Function<Image, Object>) image -> imagePopupInfoAdapter.getImagePath(image)).collect(Collectors.toList());
        if (objects.isEmpty()) return;
        new XPopup.Builder(getContext()).asImageViewer((ImageView) itemView, itemPosition, objects,
                (popupView, position1) -> {
                    popupView.updateSrcView((ImageView) imagePopupInfoAdapter.getViewByPosition(itemPosition, R.id.itemImage));
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
                    JWTParse.User user = JWTParse.INSTANCE.getUser(AppCache.Companion.getInstance().getToken());
                    String createUser = bill.getCreateUser();
                    if (createUser == null || bill.getCreateUser().equals(user.getUsername())) {
                        bill.setSynced(Constant.STATUS_DELETE);
                        AppDatabase.getInstance().billDao().update(bill);
                        AppCache.Companion.getInstance().getAppViewModule().billDelete(bill.getId());
                    } else {
                        ToastUtils.showLong("只有账单创建人有权删除该账单");
                    }
                    BillInfoPop.this.dismiss();

                }).show();

    }

    /**
     * 票据图片Adapter
     */
    public static class ImagePopupInfoAdapter extends BaseQuickAdapter<Image, BaseViewHolder> {
        ItemImgBinding binding;

        public ImagePopupInfoAdapter() {
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
}
