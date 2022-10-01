package com.rh.heji.ui.bill.create

import androidx.lifecycle.*
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.service.sync.IBillSync
import com.rh.heji.ui.base.BaseViewModelMVI
import com.rh.heji.utlis.launchIO
import java.util.*

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
class CreateBillViewModel(private val mBillSync: IBillSync) :
    BaseViewModelMVI<CreateBillAction, CreateBillUIState>() {

    var keyBoardStack: Stack<String>? = null//用于保存栈

    override fun doAction(action: CreateBillAction) {
        super.doAction(action)
        launchIO({
            when (action) {
                is CreateBillAction.Save -> {
                    save(action.bill)
                    uiState.postValue(CreateBillUIState.Close)
                }
                is CreateBillAction.SaveAgain -> {
                    save(action.bill)
                    uiState.postValue(CreateBillUIState.Reset)
                }

                is CreateBillAction.GetBill -> {
                    action.bill_id?.let {
                        val bill = App.dataBase.billImageDao().findBillAndImage(it)
                        uiState.postValue(CreateBillUIState.BillChange(bill = bill))
                    }

                }
                is CreateBillAction.GetDealers -> {
                    val users = App.dataBase.dealerDao().findAll().map {
                        it.userName
                    }.toMutableList()
                    uiState.postValue(CreateBillUIState.Dealers(users))
                }
                is CreateBillAction.GetImages -> {
                    val images = App.dataBase.imageDao().findImage(action.img_ids)
                    uiState.postValue(CreateBillUIState.Images(images))
                }
                is CreateBillAction.DeleteImage -> {
                    val image = action.image
                    App.dataBase.imageDao().preDelete(image.id)
                    mBillSync.deleteImage(image)
                }
            }
        }, {
            uiState.postValue(CreateBillUIState.Error(it))
            ToastUtils.showLong(it.message)
        })
    }

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