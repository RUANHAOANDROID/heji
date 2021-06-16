package com.rh.heji.ui.report.pop

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.databinding.LayoutBillsBinding

/**
 *Date: 2021/6/16
 *Author: 锅得铁
 *#
 */
class BottomListPop(
    context: Context,
    layoutResId: Int = R.layout.item_bill_daylist,
    data: MutableList<Bill>
) :
    BottomPopupView(context) {
    init {
        addInnerContent()
    }
    var adapter = ReportBillsAdapter(layoutResId, data)
    lateinit var recyclerView: RecyclerView
    override fun getImplLayoutId(): Int {
        return R.layout.layout_bills
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE){
            return true
        }

        return super.onTouchEvent(event)
    }


    override fun onCreate() {
        super.onCreate()
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                left = 8
                bottom = 8
                right = 8
                top = 8
                super.getItemOffsets(outRect, view, parent, state)
            }
        })
        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(
            ContextCompat.getColor(context, R.color._xpopup_light_color),
            popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f
        )
    }
}

class UnrealizedItemDecoration : RecyclerView.ItemDecoration() {

}