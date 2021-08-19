package com.rh.heji.ui.bill.adapter

import android.text.TextUtils
import android.widget.TextView
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.widget.CircleView
import com.squareup.moshi.Moshi

class DayIncomeNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = TYPE_TITLE

    override val layoutId: Int
        get() = R.layout.item_bill_dayincom

    override fun convert(helper: BaseViewHolder, item: BaseNode) {Moshi.Builder()
        // 数据类型需要自己强转
        var entity: DayIncomeNode = item as DayIncomeNode
        helper.setText(R.id.text1, "${entity.dayIncome.month}月${entity.dayIncome.monthDay}日")

        if (entity.dayIncome.weekday is String) {
            helper.setText(R.id.text2, entity.dayIncome.weekday as String)
        } else if (entity.dayIncome.weekday is Int) {
            helper.setText(R.id.text2, context.resources.getStringArray(R.array.week_day_names)[entity.dayIncome.weekday as Int])
        }

        helper.setText(R.id.text3, "支:${entity.dayIncome.expected}")
        helper.setText(R.id.text4, "收:${entity.dayIncome.income}")
    }

}

class DayBillsNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = TYPE_INFO
    override val layoutId: Int
        get() = R.layout.item_bill_daylist

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        var entity: DayBillsNode = item as DayBillsNode
        var bill = entity.bill
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

const val TYPE_TITLE = 0
const val TYPE_INFO = 1