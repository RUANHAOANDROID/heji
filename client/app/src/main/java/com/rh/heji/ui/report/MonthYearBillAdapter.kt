package com.rh.heji.ui.report

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.db.query.IncomeTimeSurplus
import com.rh.heji.databinding.ItemMonthYearBillBinding
import java.math.BigDecimal

/**
 *Date: 2021/5/18
 *Author: 锅得铁
 *#
 */
class MonthYearBillAdapter(data: MutableList<IncomeTimeSurplus>?) :
    BaseQuickAdapter<IncomeTimeSurplus, BaseViewHolder>(layoutResId = R.layout.item_month_year_bill, data) {
    lateinit var itemBinding: ItemMonthYearBillBinding
    override fun convert(holder: BaseViewHolder, item: IncomeTimeSurplus) {
        itemBinding = ItemMonthYearBillBinding.bind(holder.itemView)
        itemBinding.tvDate.text =item.time
        itemBinding.tvIncome.text =if (!item.income.toString().contains(".")) "${item.income}.00" else item.income.toString()
        itemBinding.tvExpenditure.text =if (!item.expenditure.toString().contains(".")) "${item.expenditure}.00" else item.expenditure.toString()
        itemBinding.tvSurplus.text =if (!item.surplus.toString().contains(".")) "${item.surplus}.00" else item.surplus.toString()
    }
}
