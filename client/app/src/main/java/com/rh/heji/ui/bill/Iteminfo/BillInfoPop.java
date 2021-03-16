package com.rh.heji.ui.bill.Iteminfo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.rh.heji.AppCache;
import com.rh.heji.BuildConfig;
import com.rh.heji.R;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.converters.DateConverters;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Constant;
import com.rh.heji.data.db.Image;
import com.rh.heji.databinding.PopBilliInfoBinding;
import com.rh.heji.ui.bill.img.ImageLoader;
import com.rh.heji.ui.user.JWTParse;

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
    private ImageAdapter imageAdapter;
    private PopClickListener popClickListener;

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
        binding.tvTicketTime.setText(DateConverters.date2Str(bill.getBillTime()));
        binding.rePeople.setText(bill.getDealer());
    }

    public void setBillImages(List<Image> images) {
        if (images.isEmpty()) {
            if (imageAdapter != null) {
                imageAdapter.setNewInstance(new ArrayList<>());
                imageAdapter.notifyDataSetChanged();
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
        imageAdapter.setNewInstance(imagePaths);
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
            if (popClickListener != null) {
                popClickListener.delete(bill.getId());
            }
        });
        binding.tvUpdate.setOnClickListener(v -> {
            if (popClickListener != null) {
                popClickListener.update(bill.getId());
            }
        });
        //设置圆角背景
        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(R.color._xpopup_light_color),
                popupInfo.borderRadius, popupInfo.borderRadius, 0, 0));
    }

    private void initTicketImg() {
        binding.ticketRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        imageAdapter = new ImageAdapter();
        binding.ticketRecycler.setAdapter(imageAdapter);

        imageAdapter.setDiffCallback(new DiffUtil.ItemCallback<Image>() {
            @Override
            public boolean areItemsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
                return oldItem.equals(newItem);
            }
        });
        imageAdapter.setOnItemClickListener((adapter, view, position) -> {
            showTicketImg(view, position);
        });

    }


    private void showTicketImg(View itemView, int itemPosition) {
        List<Object> objects = imageAdapter.getData().stream().map((Function<Image, Object>) image -> imageAdapter.getImagePath(image)).collect(Collectors.toList());
        if (objects.isEmpty()) return;
        new XPopup.Builder(getContext()).asImageViewer((ImageView) itemView, itemPosition, objects,
                (popupView, position1) -> {
                    popupView.updateSrcView((ImageView) imageAdapter.getViewByPosition(itemPosition, R.id.itemImage));
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
                        if (popClickListener != null) {
                            popClickListener.delete(bill.getId());
                        }
                    } else {
                        ToastUtils.showLong("只有账单创建人有权删除该账单");
                    }
                    BillInfoPop.this.dismiss();

                }).show();

    }

    public interface PopClickListener {
        void delete(String _id);

        void update(String _id);
    }

    public void setPopClickListener(PopClickListener popClickListener) {
        this.popClickListener = popClickListener;
    }
}
