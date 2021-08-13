package com.rh.heji.ui.report

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.db.d2o.IncomeTimeSurplus
import com.rh.heji.databinding.ItemMonthYearBillBinding
import java.math.BigDecimal

/**
 *Date: 2021/5/18
 *Author: 锅得铁
 *#
 */
class MonthYearBillAdapter(data: MutableList<IncomeTimeSurplus>?) :
    BaseQuickAdapter<IncomeTimeSurplus, BaseViewHolder>(
        layoutResId = R.layout.item_month_year_bill,
        data
    ) {
    lateinit var itemBinding: ItemMonthYearBillBinding
    override fun convert(holder: BaseViewHolder, item: IncomeTimeSurplus) {
        itemBinding = ItemMonthYearBillBinding.bind(holder.itemView)
        itemBinding.tvDate.text = item.time
        itemBinding.tvIncome.text = zeroPadding(item.income.toString())
        itemBinding.tvExpenditure.text = zeroPadding(item.expenditure.toString())
        itemBinding.tvSurplus.text = zeroPadding(item.surplus.toString())
        if (item.surplus?.compareTo(BigDecimal.ZERO) == -1) {//surplus<zero
            itemBinding.tvSurplus.setTextColor(ContextCompat.getColor(context, R.color.expenditure))
        } else {
            itemBinding.tvSurplus.setTextColor(ContextCompat.getColor(context, R.color.income))
        }
    }

    private fun zeroPadding(value: String): String {
        return if (!value.contains(".")) "${value}.00" else value
    }
}
