package com.rh.heji.ui.category.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.R;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.Constant;
import com.rh.heji.databinding.ItemCategoryManagerBinding;
import com.rh.heji.utlis.textdraw.ColorGenerator;
import com.rh.heji.utlis.textdraw.TextDrawable;

import org.jetbrains.annotations.NotNull;

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #分类适配器
 */
public class CategoryManagerAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {
    protected ItemCategoryManagerBinding itemBinding;

    public CategoryManagerAdapter() {
        super(R.layout.item_category_manager);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Category label) {
        itemBinding = ItemCategoryManagerBinding.bind(holder.itemView);
        int bgColor = getContext().getColor(label.selected ? R.color.category_ico_selected : R.color.category_ico);
        if (TextUtils.isEmpty(label.getCategory())) return;
        TextDrawable drawable = TextDrawable.builder().buildRound(label.getLabel().substring(0, 1), bgColor);
        itemBinding.roundImageView.setImageDrawable(drawable);
        itemBinding.tvLabel.setText(label.getLabel());
        itemBinding.btnDelete.setOnClickListener(v -> {
            label.setSynced(Constant.STATUS_DELETE);
            AppDatabase.getInstance().categoryDao().update(label);
            notifyItemChanged(getItemPosition(label));
        });
    }

}
