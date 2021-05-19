package com.rh.heji.ui.report

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.rh.heji.R
import com.rh.heji.databinding.ItemCategoryTotalBinding
import com.rh.heji.utlis.textdraw.ColorGenerator
import com.rh.heji.utlis.textdraw.TextDrawable

/**
 *Date: 2021/5/18
 *Author: 锅得铁
 *#
 */
class CategoryTotalAdapter(
    data: MutableList<PieEntry>?
) :
    BaseQuickAdapter<PieEntry, BaseViewHolder>( layoutResId= R.layout.item_category_total, data) {
    private lateinit var itemBinding: ItemCategoryTotalBinding;
    private val percentFormatter :PercentFormatter=PercentFormatter()
    override fun convert(holder: BaseViewHolder, item: PieEntry) {
        itemBinding = ItemCategoryTotalBinding.bind(holder.itemView)
        val drawable = TextDrawable.builder().buildRound(item.label.substring(0, 1), ColorGenerator.MATERIAL.randomColor)
        itemBinding.imgCategory.setImageDrawable(drawable)
        itemBinding.tvCategory.text =item.label
        itemBinding.tvCategoryIncome.text =item.data.toString()
        itemBinding.tvCategoryPercentage.text ="${percentFormatter.getFormattedValue(item.value)}"
    }
}