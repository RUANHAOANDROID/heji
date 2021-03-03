package com.rh.heji.ui.bill.adapter

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R

class DayIncomeNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 0

    override val layoutId: Int
        get() = R.layout.item_bill_dayincom

    override fun convert(helper: BaseViewHolder, node: BaseNode) {
        // 数据类型需要自己强转
        var entity: DayIncomeNode = node as DayIncomeNode
        helper.setText(R.id.text1,"test")
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        getAdapter()!!.expandOrCollapse(position)
    }
}

class DayBillsNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.item_bill_daylist

    override fun convert(helper: BaseViewHolder, node: BaseNode) {
        var entity: DayBillsNode = node as DayBillsNode
        helper.setText(R.id.text,"test")
    }
}