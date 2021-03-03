package com.rh.heji.ui.bill.adapter

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayBillsNodeProvider
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.ui.bill.adapter.DayIncomeNodeProvider


class NodeBillsAdapter() : BaseNodeAdapter() {
    init {
        addNodeProvider(DayIncomeNodeProvider())
        addNodeProvider(DayBillsNodeProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        val node = data[position]
        if (node is DayIncomeNode) {
            return 0
        } else if (node is DayBillsNode) {
            return 1
        }
        return -1
    }
}