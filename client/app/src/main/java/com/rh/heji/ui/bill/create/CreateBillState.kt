package com.rh.heji.ui.bill.create

import androidx.lifecycle.MutableLiveData
import com.rh.heji.data.db.Bill

/**
 * Popup基础
 * @date 2022/8/26
 * @author 锅得铁
 * @since v1.0
 */
sealed class CreateBillUIState {
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

}

sealed class CreateBillEvent {

    /**
     * 获取Bill
     */
    class GetBill(var bill_id: String? = null) : CreateBillEvent()

    /**
     * 保存Bill至数据库
     */
    class Save(val bill: Bill) : CreateBillEvent()

    /**
     * 保存Bill至数据库
     */
    class SaveAgain(val bill: Bill) : CreateBillEvent()

    /**
     * 获取经手人
     */
    class GetDealers(val bill_id: String) : CreateBillEvent()
}