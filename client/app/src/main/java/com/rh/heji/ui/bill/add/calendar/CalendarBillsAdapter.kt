package com.rh.heji.ui.bill.add.calendar

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.data.db.Bill

class CalendarBillsAdapter(layoutResId: Int, data: MutableList<Bill>?) : BaseQuickAdapter<Bill, BaseViewHolder>(layoutResId, data) {
    init {

    }

    override fun convert(holder: BaseViewHolder, item: Bill) {
    }
}