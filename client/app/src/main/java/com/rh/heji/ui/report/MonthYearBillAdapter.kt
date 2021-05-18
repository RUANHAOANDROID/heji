package com.rh.heji.ui.report

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.databinding.ItemMonthYearBillBinding

/**
 *Date: 2021/5/18
 *Author: 锅得铁
 *#
 */
class MonthYearBillAdapter(data: MutableList<Bill>?) :
    BaseQuickAdapter<Bill, BaseViewHolder>(layoutResId = R.layout.item_month_year_bill, data) {
    lateinit var itemBinding: ItemMonthYearBillBinding
    override fun convert(holder: BaseViewHolder, item: Bill) {
        itemBinding = ItemMonthYearBillBinding.bind(holder.itemView)
        itemBinding
    }
}

data class MonthYearBill(
    var date: String,
    var income: String,
    var exception: String,
    var surplus: String
)