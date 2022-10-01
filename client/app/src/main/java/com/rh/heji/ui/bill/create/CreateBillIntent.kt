package com.rh.heji.ui.bill.create

import com.rh.heji.data.db.Bill
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
    //    class SelectCategory(category: String?) : AddBillUIState()
//    class Remark(remark: String?) : AddBillUIState()
//    class Money(money: BigDecimal) : AddBillUIState()
//    class Dealer(dealer: String?) : AddBillUIState()
//    class Images(images: List<String>) : AddBillUIState()
//    class Time(billTime: Date) : AddBillUIState()
//    class SaveAgain(bill: Bill) : AddBillUIState()
//    class Save(bill: Bill) : AddBillUIState()
    class BillChange(val bill: Bill) : CreateBillUIState()
    object Close : CreateBillUIState()

    /**
     * 重置页面Save Again的时候调用
     */
    object Reset : CreateBillUIState()

    class Error(val throws: Throwable) : CreateBillUIState()

    class Dealers(val dealers: MutableList<String>) : CreateBillUIState()

    class Images(val images: MutableList<Image>) : CreateBillUIState()


}

sealed class CreateBillAction : IAction {

    /**
     * 获取Bill
     */
    class GetBill(var bill_id: String? = null) : CreateBillAction()

    /**
     * 保存Bill至数据库
     */
    class Save(val bill: Bill) : CreateBillAction()

    /**
     * 保存Bill至数据库
     */
    class SaveAgain(val bill: Bill) : CreateBillAction()

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
}