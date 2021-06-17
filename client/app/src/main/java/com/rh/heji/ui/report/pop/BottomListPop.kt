package com.rh.heji.ui.report.pop

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.lxj.xpopup.widget.VerticalRecyclerView
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
    lateinit var recyclerView: VerticalRecyclerView
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
        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(
            ContextCompat.getColor(context, R.color._xpopup_light_color),
            popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f
        )
        recyclerView.adapter = adapter
    }

//    override fun getMaxHeight(): Int {
//        return ScreenUtils.getAppScreenHeight()
//    }
    //动态高度
    override fun getPopupHeight(): Int {
        return super.getPopupHeight()
    }
}

class UnrealizedItemDecoration : RecyclerView.ItemDecoration() {

}