package com.rh.heji.ui.bill.add

import com.rh.heji.data.db.Bill
import java.math.BigDecimal
import java.util.*

/**
 * 账单添加UI接口
 * @date 2022/5/12
 * @author 锅得铁
 * @since v1.0
 */
interface IAddBillUIState {
    fun initBill(bill: Bill) {
        setCategory(bill.category)
        setRemark(bill.remark)
        setMoney(bill.money)
        setDealer(bill.dealer)
        setImages(bill.images)
        setTime(bill.billTime)
    }

    fun setCategory(category: String?)
    fun setRemark(remark: String?)
    fun setMoney(money: BigDecimal)
    fun setDealer(dealer: String?)
    fun setImages(images: List<String>)
    fun setTime(billTime: Date)
    fun saveAgain(bill: Bill)
    fun save(bill: Bill)
}