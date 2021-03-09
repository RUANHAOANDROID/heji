package com.rh.heji.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TimeUtils
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.ui.bill.adapter.DayIncome
import java.math.BigDecimal


class DayListCardView : CardView {
    var recyclerView: RecyclerView? = null
    private fun init(context: Context) {
        recyclerView = RecyclerView(context)
        recyclerView?.overScrollMode = OVER_SCROLL_NEVER
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    var fakeData = mutableListOf<ProviderMultiEntity>()
        get() {
            var a = 28
            while (a-- < 1) {
                var dayIncome = DayIncome("100", "100", a, a, a)
                var bill = Bill()
                bill.remark = "aa"
                bill.money = BigDecimal(100)
                bill.billTime = com.blankj.utilcode.util.TimeUtils.getNowDate()
                bill.type = 1
                bill.category = "test"
                bill.imgCount = 3
                var entity = ProviderMultiEntity(dayIncome, bill)
                field.add(entity)
            }
            return field
        }
}

class adapter : BaseProviderMultiAdapter<ProviderMultiEntity>() {
    init {
        addItemProvider(TitleItemProvider())
        addItemProvider(InfoItemProvider())
    }

    override fun getItemType(data: List<ProviderMultiEntity>, position: Int): Int {
        return if (position == 0) ProviderMultiEntity().TITLE else ProviderMultiEntity().INFO
    }
}

class TitleItemProvider : BaseItemProvider<ProviderMultiEntity>() {
    // item 类型
    override val itemViewType: Int
        get() = ProviderMultiEntity(null).TITLE

    // 返回 item 布局 layout
    override val layoutId: Int
        get() = R.layout.item_bill_dayincom

    override fun convert(helper: BaseViewHolder, data: ProviderMultiEntity) {
        var dayIncome: DayIncome? = data.dayIncome
        dayIncome?.let {
            helper.setText(R.id.text1, "${dayIncome.month}月${dayIncome.monthDay}日")

            helper.setText(R.id.text2, context.resources.getStringArray(R.array.week_day_names)[Integer.valueOf(dayIncome.weekday)])
            helper.setText(R.id.text3, "支:${dayIncome.expected}")
            helper.setText(R.id.text4, "收:${dayIncome.income}")
        }
    }

    // 点击 item 事件
    override fun onClick(helper: BaseViewHolder, view: View, data: ProviderMultiEntity, position: Int) {

    }
}

class InfoItemProvider : BaseItemProvider<ProviderMultiEntity>() {
    // item 类型
    override val itemViewType: Int
        get() = ProviderMultiEntity(null).INFO

    // 返回 item 布局 layout
    override val layoutId: Int
        get() = R.layout.item_bill_daylist

    override fun convert(helper: BaseViewHolder, data: ProviderMultiEntity) {
        var bill = data.bill
        bill?.let {
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

    // 点击 item 事件
    override fun onClick(helper: BaseViewHolder, view: View, data: ProviderMultiEntity, position: Int) {

    }
}

data class ProviderMultiEntity(var dayIncome: DayIncome? = null, var bill: Bill? = null) {
    val TITLE = 1
    val INFO = 3
}
