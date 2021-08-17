package com.rh.heji.ui.report.pop

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.lxj.xpopup.widget.VerticalRecyclerView
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.ui.bill.iteminfo.BillInfoPop
import com.rh.heji.ui.bill.iteminfo.BillPopClickListenerImpl

/**
 *Date: 2021/6/16
 *Author: 锅得铁
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

    var adapter = ReportBillsAdapter(layoutResId, data)
    val recyclerView: VerticalRecyclerView by lazy { findViewById(R.id.recycler) }
    val titleView: TextView by lazy { findViewById(R.id.tvTitle) }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_bills
    }

    override fun onCreate() {
        super.onCreate()

        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(
            ContextCompat.getColor(context, R.color._xpopup_light_color),
            popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f
        )
        adapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as Bill
            val billInfoPop = BillInfoPop(activity= activity,bill = item, object : BillPopClickListenerImpl() {
                override fun delete(bill: Bill) {
                    super.delete(bill)
                    adapter.removeAt(position)
                    if (adapter.data.size <= 0) {
                        dismiss()
                    } else {
                        adapter.notifyItemRemoved(position)
                    }
                }

                override fun update(bill: Bill) {
                    super.update(bill)
                    dismiss()
                }

            })
            XPopup.Builder(context).asCustom(billInfoPop).show()

        }
        recyclerView.adapter = adapter
    }

}