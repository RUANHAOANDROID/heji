package com.rh.heji.ui.create

import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Image
import com.rh.heji.ui.base.IAction
import com.rh.heji.ui.base.IUiState

/**
 * Popup基础
 * @date 2022/8/26
 * @author 锅得铁
 * @since v1.0
 */
sealed class CreateBillUIState : IUiState {
//    class Remark(remark: String?) : AddBillUIState()
//    class Money(money: BigDecimal) : AddBillUIState()


//    class Time(billTime: Date) : AddBillUIState()

    class Save(val again: Boolean) : CreateBillUIState()
    class BillChange(val bill: Bill) : CreateBillUIState()
    class Error(val throws: Throwable) : CreateBillUIState()
    class Dealers(val dealers: MutableList<String>) : CreateBillUIState()
    class Images(val images: MutableList<Image>) : CreateBillUIState()
    class Categories(val type: Int, val categories: MutableList<Category>) : CreateBillUIState()
}

sealed class CreateBillAction : IAction {

    /**
     * 获取Bill
     */
    class GetBill(var bill_id: String? = null) : CreateBillAction()

    /**
     * 保存Bill至数据库
     */
    class Save(val bill: Bill, val again: Boolean) : CreateBillAction()

    /**
     * 获取经手人
     */
    class GetDealers(val bill_id: String) : CreateBillAction()

    /**
     * 获取账单图片
     */
    class GetImages(val img_ids: List<String>) : CreateBillAction()

    /**
     * 删除账单图片
     */
    class DeleteImage(val image: Image) : CreateBillAction()

    /**
     * 获取账本下收入或支出类别标签
     */
    class GetCategories(val type: Int) : CreateBillAction()
}