package com.rh.heji.ui.bill.create

import androidx.lifecycle.*
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.service.sync.IBillSync
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO
import java.util.*

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
class CreateBillViewModel(private val mBillSync: IBillSync) : BaseViewModel() {

    private val uiStateLiveData = MutableLiveData<CreateBillUIState>()

    fun subUIState(): LiveData<CreateBillUIState> {
        return uiStateLiveData
    }

    var keyBoardStack: Stack<String>? = null//用于保存栈

    fun eventState(event: CreateBillEvent) = when (event) {
        is CreateBillEvent.Save -> {
            save(event.bill)
        }
        is CreateBillEvent.SaveAgain -> {
            save(event.bill)
        }

        is CreateBillEvent.GetBill -> {
            event.bill_id?.let {
                launchIO({
                    val bill = App.dataBase.billImageDao().findBillAndImage(it)
                    uiStateLiveData.postValue(CreateBillUIState.BillChange(bill = bill))
                })

            }
        }

        is CreateBillEvent.GetDealers -> {
            launchIO({
                val users = App.dataBase.dealerDao().findAll().map {
                    it.userName
                }.toMutableList()
                uiStateLiveData.postValue(CreateBillUIState.Dealers(users))
            })
        }
    }

    /**
     * 保存账单到本地
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    private fun save(bill: Bill) {
        launchIO({
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
            var count: Long =
                App.dataBase.billImageDao().installBillAndImage(bill, images)
            mBillSync.add(bill)
            uiStateLiveData.postValue(CreateBillUIState.Close)
        }, {
            ToastUtils.showLong(it.message)
            uiStateLiveData.postValue(CreateBillUIState.Error(it))
        })
    }
}