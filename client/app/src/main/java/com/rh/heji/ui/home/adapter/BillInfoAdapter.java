package com.rh.heji.ui.home.adapter;

import android.view.View;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.R;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Bill;
import com.rh.heji.Constants;
import com.rh.heji.databinding.ItemBillBinding;

import org.jetbrains.annotations.NotNull;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
public class BillInfoAdapter extends BaseQuickAdapter<Bill, BaseViewHolder> {
    public static final int ITEM_DATE = 1;
    public static final int ITEM_INFO = 2;

    public BillInfoAdapter() {
        super(R.layout.item_bill);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Bill billTab) {
        ItemBillBinding binding = ItemBillBinding.bind(holder.itemView);
        binding.tvTicketTime.setText("小票时间：未知");
        if (billTab.getBillTime() != 0) {
            binding.tvTicketTime.setText("小票时间：" + TimeUtils.millis2String(billTab.getBillTime(), "yyyy-MM-dd HH:mm"));
        }
        if (billTab.getType() == BillType.EXPENDITURE.type()) {
            binding.tvSZ.setText("- " + billTab.getMoney());
            binding.tvSZ.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_light));
        } else {
            binding.tvSZ.setText("+ " + billTab.getMoney());
            binding.tvSZ.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_light));
        }

        binding.tvUserLabel.setText(billTab.getDealer());
        binding.tvBillTime.setText(getTime(billTab));
        binding.tvSzLabel.setText(billTab.getCategory());
        binding.tvRemark.setText(billTab.getRemark());
        if (billTab.getImgCount() > 0) {
            binding.tvTicketSize.setVisibility(View.VISIBLE);
            binding.tvTicketSize.setText(billTab.getImgCount() + "");
        } else {
            binding.tvTicketSize.setVisibility(View.GONE);
        }
        binding.getRoot().getBackground().setAlpha(Constants.BACKGROUND_ALPHA);
    }

    private String getTime(Bill billTab) {
        long time = 0;
        if (0 != billTab.getBillTime()) {
            time = billTab.getBillTime();
        } else {
            time = billTab.getCreateTime();
        }
        String stringTime = TimeUtils.millis2String(time, "yyyy-MM-dd HH:mm");
        return stringTime;
    }
}
