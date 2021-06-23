package com.rh.heji.ui.bill.category.adapter

import android.text.TextUtils
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.ItemCategoryManagerBinding
import com.rh.heji.utlis.textdraw.TextDrawable

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #分类适配器
 */
open class CategoryManagerAdapter :
    BaseQuickAdapter<Category, BaseViewHolder>(R.layout.item_category_manager) {

    protected lateinit var itemBinding: ItemCategoryManagerBinding
    override fun convert(holder: BaseViewHolder, category: Category) {
        itemBinding = ItemCategoryManagerBinding.bind(holder.itemView)
        val bgColor =
            context.getColor(if (category.selected) R.color.category_ico_selected else R.color.category_ico)
        if (TextUtils.isEmpty(category.category)) return
        val drawable = TextDrawable.builder().buildRound(category.category.substring(0, 1), bgColor)
        itemBinding.roundImageView.setImageDrawable(drawable)
        itemBinding.tvName.text = category.category
        addChildClickViewIds(itemBinding!!.btnDelete.id)
        //        itemBinding.btnDelete.setOnClickListener(v -> {
//            deleteCategory(category);
//        });
        if (category.category == "其他") {
            itemBinding.btnEdit.visibility = View.INVISIBLE
            itemBinding.btnDelete.visibility = View.INVISIBLE
        } else {
            itemBinding.btnEdit.visibility = View.VISIBLE
            itemBinding.btnDelete.visibility = View.VISIBLE
        }
    }
}