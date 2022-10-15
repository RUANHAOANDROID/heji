package com.rh.heji.ui.popup

import android.text.TextUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.lxj.xpopup.widget.VerticalRecyclerView
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.widget.CircleView

/**
 * 统计底部
 *@date: 2021/6/16
 *@author: 锅得铁
 *#
 */
class BottomListPop(
    val activity: MainActivity,
    layoutResId: Int = R.layout.item_bill_daylist,
    data: MutableList<Bill>
) :
    BottomPopupView(activity) {
    init {
        addInnerContent()
    }

    private var adapter = ReportBillsAdapter(layoutResId, data)
    private val recyclerView: VerticalRecyclerView by lazy { findViewById(R.id.recycler) }
    val title: TextView by lazy { findViewById(R.id.tvTitle) }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_layout_bills
    }

    private lateinit var billInfoPop: PopupBillInfo
    override fun onCreate() {
        super.onCreate()
        LogUtils.d("${this.javaClass.name} create")
        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(
            ContextCompat.getColor(context, R.color._xpopup_light_color),
            popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f
        )
        adapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as Bill
            billInfoPop = PopupBillInfo.create(activity = activity, delete = {

                adapter.removeAt(position)
                if (adapter.data.size <= 0) {
                    dismiss()
                } else {
                    adapter.notifyItemRemoved(position)
                }
            },
                update = {
                    dismiss()
                })
            billInfoPop.show(bill = item)
        }
        recyclerView.adapter = adapter
    }

}

internal class ReportBillsAdapter(layoutResId: Int, data: MutableList<Bill>) :
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