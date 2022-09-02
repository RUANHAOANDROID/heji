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

    fun eventState(event: CreateBillEvent) = launchIO({
        when (event) {
            is CreateBillEvent.Save -> {
                save(event.bill)
                uiStateLiveData.postValue(CreateBillUIState.Close)
            }
            is CreateBillEvent.SaveAgain -> {
                save(event.bill)
                uiStateLiveData.postValue(CreateBillUIState.Reset)
            }

            is CreateBillEvent.GetBill -> {
                event.bill_id?.let {
                    val bill = App.dataBase.billImageDao().findBillAndImage(it)
                    uiStateLiveData.postValue(CreateBillUIState.BillChange(bill = bill))
                }

            }
            is CreateBillEvent.GetDealers -> {
                val users = App.dataBase.dealerDao().findAll().map {
                    it.userName
                }.toMutableList()
                uiStateLiveData.postValue(CreateBillUIState.Dealers(users))
            }
            is CreateBillEvent.GetImages -> {
                val images = App.dataBase.imageDao().findImage(event.img_ids)
                uiStateLiveData.postValue(CreateBillUIState.Images(images))
            }
            is CreateBillEvent.DeleteImage->{
                val imageId =event.imageId
                App.dataBase.imageDao().preDelete(imageId)
            }
        }
    }, {
        uiStateLiveData.postValue(CreateBillUIState.Error(it))
        ToastUtils.showLong(it.message)
    })

    /**
     * 保存账单到本地
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    private suspend fun save(bill: Bill) {
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
    }
}