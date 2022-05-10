package com.rh.heji.ui.bill.add

import androidx.lifecycle.*
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.App.Companion.currentBook
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
class AddBillViewModel : BaseViewModel() {

    private var bill: Bill = Bill()

    val billDao = App.dataBase.billDao()

    var keyBoardStack: Stack<String>? = null//用于保存栈
    private var billLiveData: MutableLiveData<Bill> = MutableLiveData()

    fun billChanged(): LiveData<Bill> {
        return billLiveData
    }

    fun getBill(): Bill {
        return bill
    }

    fun setBill(bill: Bill) {
        this.bill = bill
        billLiveData.postValue(bill)
    }

    fun setCategory(category: Category) {
        bill.type = category.type
        bill.category = category.category
        //billLiveData.postValue(bill)
    }

    fun setRemark(remark: String) {
        bill.remark = remark
        //billLiveData.postValue(bill)
    }

    fun setMoney(money: String) {
        bill.money = BigDecimal(money)
        //billLiveData.postValue(bill)
    }

    fun setDealer(dealer: String) {
        bill.dealer =dealer
        //billLiveData.postValue(bill)
    }

    fun setImages(images: MutableList<String>) {
        bill.images = images
        billLiveData.postValue(bill)
    }

    fun setTime(time: Date) {
        bill.billTime = time
        //billLiveData.postValue(bill)
    }

    private fun resetBill() {
        billLiveData.postValue(bill)
        bill = Bill().apply {
            bookId = currentBook!!.id
            createTime = System.currentTimeMillis()
        }
    }

    /**
     * 保存账单到本地
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    fun save(saveCall: (Bill) -> Unit) {

        val images = mutableListOf<Image>()
        if (bill.images.isNotEmpty()) {
            val selectImages = bill.images.map { s: String? ->
                val image = Image(ObjectId().toString(), bill.id)
                image.localPath = s
                image.synced = STATUS.NOT_SYNCED
                image
            }.toMutableList()
            images.addAll(selectImages)
        }

        launch({
            var count: Long =
                App.dataBase.billImageDao().installBillAndDao(bill, images)
            if (count > 0) {
                saveCall(bill.copy())
                resetBill()//最后重新赋值ID
            }
        }, {
            ToastUtils.showShort(it.message)
        })
    }


    fun getDealers(): MutableLiveData<MutableList<String>> {
        return dealersLiveData
    }

    private val dealersLiveData by lazy { MutableLiveData<MutableList<String>>().also { loadDealers() } }
    private fun loadDealers() {
        launchIO({
            val users = App.dataBase.dealerDao().findAll().map {
                it.userName
            }.toMutableList()
            dealersLiveData.postValue(users)
        })
    }

    fun getBillImages(bid: String = bill.id): LiveData<MutableList<Image>> {
        return App.dataBase.imageDao().findByBillId(bid)
            .asLiveData(viewModelScope.coroutineContext)
    }
}