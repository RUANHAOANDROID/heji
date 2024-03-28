package com.hao.heji.ui.home

import com.chad.library.adapter.base.entity.node.BaseNode
import com.hao.heji.data.db.Image
import com.hao.heji.data.db.dto.Income
import com.hao.heji.ui.base.IAction
import com.hao.heji.ui.base.IUiState
import com.hao.heji.utils.YearMonth

/**
 *
 * @date 2022/10/1
 * @author 锅得铁
 * @since v1.0
 */
internal sealed interface BillListUiState : IUiState {
    class Bills(val nodeList: MutableList<BaseNode>) : BillListUiState
    class Summary(val income: Income) : BillListUiState
    class Error(val t: Throwable) : BillListUiState
    class Images(val data: MutableList<Image>):BillListUiState
}

internal sealed class BillListAction : IAction {
    object Refresh : BillListAction()
    class Summary(val yearMonth: YearMonth) : BillListAction()
    class MonthBill(val yearMonth: YearMonth) : BillListAction()
    class GetImages(val bid: String) : BillListAction()
//    class YearBill(val year: String) : BillListAction()
}