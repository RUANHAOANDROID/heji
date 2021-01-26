package com.rh.heji.ui.bill.add.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.R;
import com.rh.heji.databinding.ItemPopSelectTicketImageBinding;
import com.rh.heji.utlis.GlideUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
public class TicketPhotoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    private ItemPopSelectTicketImageBinding binding;

    public TicketPhotoAdapter() {
        super(R.layout.item_pop_select_ticket_image, new ArrayList<String>());
        addChildClickViewIds(R.id.imgDelete);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder viewHolder, String item) {
         binding = ItemPopSelectTicketImageBinding.bind(viewHolder.itemView);

        if (item.equals("+")) {
            binding.imgTicket.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_add_image_red_32dp));
            binding.imgDelete.setVisibility(View.INVISIBLE);
        } else {
            GlideUtils.loadImageFile(getContext(), item, binding.imgTicket);
            binding.imgDelete.setVisibility(View.VISIBLE);
        }

    }
}
