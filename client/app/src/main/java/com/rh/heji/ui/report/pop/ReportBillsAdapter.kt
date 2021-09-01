package com.rh.heji.ui.report.pop

import android.text.TextUtils
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.widget.CircleView

class ReportBillsAdapter(layoutResId: Int, data: MutableList<Bill>) :
    BaseQuickAdapter<Bill, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: Bill) {
        var bill = item
        var incomeColor =
            if (bill.type == -1) context.getColor(R.color.expenditure) else context.getColor(R.color.income)

        holder.getView<CircleView>(R.id.circleView).setColor(incomeColor)
        holder.setText(R.id.tvCategory, bill.category)
        holder.setText(R.id.tvMoney, "${if (bill.type == -1) "- " else "+ "}${bill.money}")
        holder.setTextColor(R.id.tvMoney, incomeColor)
        holder.setText(R.id.tvInfo, bill.remark)
        if (bill.images.isNotEmpty()) {
            holder.getView<TextView>(R.id.tvInfo).setCompoundDrawablesWithIntrinsicBounds(
                context.getDrawable(R.drawable.ic_baseline_image_18),
                null,
                null,
                null
            )
        } else {
            holder.getView<TextView>(R.id.tvInfo)
                .setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        holder.setGone(R.id.tvInfo, TextUtils.isEmpty(bill.remark) && bill.images.isEmpty())

    }

}