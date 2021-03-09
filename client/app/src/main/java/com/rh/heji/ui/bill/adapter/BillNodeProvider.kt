package com.rh.heji.ui.bill.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.widget.CircleView

class DayIncomeNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 0

    override val layoutId: Int
        get() = R.layout.item_bill_dayincom

    override fun convert(helper: BaseViewHolder, node: BaseNode) {
        // 数据类型需要自己强转
        var entity: DayIncomeNode = node as DayIncomeNode
        helper.setText(R.id.text1, "${entity.dayIncome.month}月${entity.dayIncome.monthDay}日")

        helper.setText(R.id.text2, context.resources.getStringArray(R.array.week_day_names)[Integer.valueOf(entity.dayIncome.weekday)])
        helper.setText(R.id.text3, "支:${entity.dayIncome.expected}")
        helper.setText(R.id.text4, "收:${entity.dayIncome.income}")
    }

}

class DayBillsNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.item_bill_daylist

    override fun convert(helper: BaseViewHolder, node: BaseNode) {
        var entity: DayBillsNode = node as DayBillsNode
        var bill = entity.bill
        if (entity.bill.type == -1) {

        }
        var incomeColor = if (bill.type == -1) context.getColor(R.color.expenditure) else context.getColor(R.color.income)

        helper.getView<CircleView>(R.id.circleView).setColor(incomeColor)
        helper.setText(R.id.tvCategory, bill.category)
        helper.setText(R.id.tvMoney, "${if (bill.type == -1) "- " else "+ "}${bill.money}")
        helper.setTextColor(R.id.tvMoney, incomeColor)
        helper.setText(R.id.tvInfo, bill.remark)
        if (bill.imgCount > 0) {
            helper.getView<TextView>(R.id.tvInfo).setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_baseline_image_18), null, null, null)
        } else {
            helper.getView<TextView>(R.id.tvInfo).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        helper.setGone(R.id.tvInfo, TextUtils.isEmpty(bill.remark) && bill.imgCount == 0)

    }
}