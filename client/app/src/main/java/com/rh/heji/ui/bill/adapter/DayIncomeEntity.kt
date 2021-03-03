package com.rh.heji.ui.bill.adapter

import com.chad.library.adapter.base.entity.node.BaseNode
import com.rh.heji.data.db.Bill


/**
 * 日收益Object
 */
data class DayIncome(var expected: String,
                     var income: String,
                     var monthDay: String,
                     var weekday: String)


/**
 * 日收益Node,子节点为账单List
 */
class DayIncomeNode(private val dayListNodes: MutableList<BaseNode>, val dayIncome: DayIncome) : BaseNode() {
    override val childNode: MutableList<BaseNode>?
        get() = dayListNodes
}

/**
 * 当日账单List
 */
class DayBillsNode(var bill: Bill) : BaseNode() {
    override val childNode: MutableList<BaseNode>?
        get() = null
}
