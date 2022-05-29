package com.rh.heji.ui.bill.add

import androidx.lifecycle.*
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO
import java.util.*

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
class AddBillViewModel : BaseViewModel() {

    private val saveLiveData = MutableLiveData<Int>()

    fun getSaveResult(): LiveData<Int> {
        return saveLiveData
    }

    var keyBoardStack: Stack<String>? = null//用于保存栈

    /**
     * 保存账单到本地
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    fun save(bill: Bill, state: Int) {
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
            saveLiveData.postValue(state)
        }, {
            ToastUtils.showLong(it.message)
            saveLiveData.postValue(AddBillFragment.SAVE_ERROR)
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

    fun getBillImages(bid: String): LiveData<MutableList<Image>> {
        return App.dataBase.imageDao().findByBillId(bid)
            .asLiveData(viewModelScope.coroutineContext)
    }
}