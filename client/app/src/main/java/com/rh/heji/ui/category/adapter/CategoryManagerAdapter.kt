package com.rh.heji.ui.category.adapter

import android.text.TextUtils
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.ItemCategoryManagerBinding
import com.rh.heji.utils.textdraw.TextDrawable

/**
 * @date: 2020/9/16
 * @author: 锅得铁
 * #分类适配器
 */
open class CategoryManagerAdapter :
    BaseQuickAdapter<Category, BaseViewHolder>(R.layout.item_category_manager) {

    protected lateinit var itemBinding: ItemCategoryManagerBinding
    override fun convert(holder: BaseViewHolder, category: Category) {
        itemBinding = ItemCategoryManagerBinding.bind(holder.itemView)
        val bgColor = context.getColor(if (category.isSelected) R.color.category_ico_selected else R.color.category_ico)

        if (category.name.isEmpty()) {
            return
        }
        val drawable = TextDrawable.builder().buildRound(category.name[0].toString(), bgColor)
        itemBinding.roundImageView.setImageDrawable(drawable)
        itemBinding.tvName.text = category.name
        addChildClickViewIds(itemBinding.btnDelete.id)
        itemBinding.btnEdit.visibility = if (category.name == "其他") View.INVISIBLE else View.VISIBLE
        itemBinding.btnDelete.visibility = if (category.name == "其他") View.INVISIBLE else View.VISIBLE

    }
}