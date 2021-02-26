package com.rh.heji.ui.category.adapter;

import android.graphics.Color;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.R;
import com.rh.heji.data.db.Category;
import com.rh.heji.databinding.ItemCategoryBinding;
import com.rh.heji.utlis.textdraw.ColorGenerator;
import com.rh.heji.utlis.textdraw.TextDrawable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #分类适配器
 */
public class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {
    public static final String SETTING = "管理";
    protected ItemCategoryBinding itemBinding;

    public CategoryAdapter(List<Category> categories) {
        super(R.layout.item_category,categories);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Category label) {
        itemBinding = ItemCategoryBinding.bind(holder.itemView);
        int bgColor =getContext().getColor(label.selected?R.color.category_ico_selected:R.color.category_ico);
        if (TextUtils.isEmpty(label.getCategory()))return;
        TextDrawable drawable = TextDrawable.builder().buildRound(label.getCategory().substring(0, 1), bgColor);
        itemBinding.roundImageView.setImageDrawable(drawable);
        itemBinding.tvLabel.setText(label.getCategory());
        if (label.getCategory().equals(SETTING)) {
            itemBinding.roundImageView.setImageDrawable(
                    TextDrawable.builder().buildRound(SETTING,
                            ColorGenerator.MATERIAL.getRandomColor()));
            itemBinding.tvLabel.setText(SETTING);
            itemBinding.roundImageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_settings_24));
        }
    }

}
