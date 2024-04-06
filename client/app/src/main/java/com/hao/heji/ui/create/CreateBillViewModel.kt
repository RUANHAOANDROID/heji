package com.hao.heji.ui.create

import androidx.lifecycle.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.hao.heji.App
import com.hao.heji.config.Config
import com.hao.heji.data.db.*
import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.service.ws.SyncPusher
import com.hao.heji.ui.base.BaseViewModel
import com.hao.heji.utils.launchIO
import java.util.*

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
@PublishedApi
internal class CreateBillViewModel(private val syncPusher: SyncPusher?) :
    BaseViewModel<CreateBillAction, CreateBillUIState>() {

    var keyBoardStack: Stack<String>? = null//用于保存栈

    override fun doAction(action: CreateBillAction) {

        launchIO({
            LogUtils.d(TimeUtils.millis2String(System.currentTimeMillis(),"yyyy/MM/dd HH:mm:ss"))
            when (action) {
                is CreateBillAction.Save -> {
                    save(action.bill)
                    send(CreateBillUIState.Save(action.again))
                }
                is CreateBillAction.GetBill -> {
                    action.bill_id?.let {
                        val bill = App.dataBase.billImageDao().findBillAndImage(it)
                        send(CreateBillUIState.BillChange(bill = bill))
                    }

                }
                is CreateBillAction.GetDealers -> {
                    val users = App.dataBase.dealerDao().findAll().map {
                        it.userName
                    }.toMutableList()
                    send(CreateBillUIState.Dealers(users))
                }
                is CreateBillAction.GetImages -> {
                    val images = App.dataBase.imageDao().findImage(action.img_ids)
                    send(CreateBillUIState.Images(images))
                }
                is CreateBillAction.DeleteImage -> {
                    val image = action.image
                    App.dataBase.imageDao().preDelete(image.id)
//                    mBillSync.deleteImage(image)
                }
                is CreateBillAction.GetCategories -> {
                    LogUtils.d(
                        "TimeTest",
                        TimeUtils.millis2String(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss")
                    )
                    val categories = App.dataBase.categoryDao()
                        .findIncomeOrExpenditure(Config.book.id, action.type)
                    send(CreateBillUIState.Categories(action.type, categories))
                    LogUtils.d(
                        "TimeTest",
                        categories,
                        TimeUtils.millis2String(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss")
                    )
                }
            }
        }, {
            send(CreateBillUIState.Error(it))
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
                image.syncStatus = STATUS.NOT_SYNCED
                image
            }.toMutableList()
            images.addAll(selectImages)
            var count: Long =
                App.dataBase.billImageDao().installBillAndImage(bill, images)
        }else{
            App.dataBase.billDao().install(bill)
        }
        syncPusher?.addBill(bill)
    }
}