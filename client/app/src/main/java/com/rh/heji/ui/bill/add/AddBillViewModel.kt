package com.rh.heji.ui.bill.add

import androidx.lifecycle.*
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.currentBook
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.CRUD
import com.rh.heji.data.DataBus
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launch
import com.rh.heji.utlis.launchIO
import com.rh.heji.utlis.runMainThread
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
class AddBillViewModel : BaseViewModel() {
    val billDao = AppDatabase.getInstance().billDao()
    var imgUrls = mutableListOf<String>()
        set(value) {
            field = value
            imgUrlsLive.postValue(imgUrls)
        }

    private var imgUrlsLive = MutableLiveData<MutableList<String>>() // image live
    fun imagesChanged() = imgUrlsLive

    var bill: Bill = Bill()
        set(value) {
            field = value
            billLiveData.postValue(field)
        }
    var keyBoardStack: Stack<String>? = null//用于保存栈
    private var billLiveData: MutableLiveData<Bill> = MutableLiveData()

    fun billChanged(): MutableLiveData<Bill> {
        return billLiveData
    }

    /**
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    fun save(money: String, category: Category, saveCall: (Bill) -> Unit) {
        val images = imgUrls.stream().map { s: String? ->
            val image = Image(ObjectId().toString(), bill.id)
            image.localPath = s
            image.synced = STATUS.NOT_SYNCED
            image
        }.collect(Collectors.toList())
        bill.apply {
            bookId = currentBook.id
            //id = billId
            this.money = BigDecimal(money)
            createTime = System.currentTimeMillis()
            bill.type = category.type
            bill.category = category.category
        }

        launch({
           val count = billDao.install(bill)
            AppDatabase.getInstance().imageDao().install(images)
            if (count > 0) {
                ToastUtils.showShort("已保存: ${bill.category + money}  ")
            }
            saveCall(bill)
            bill.id = ObjectId.get().toHexString()//保存重新赋值ID
            DataBus.post(CRUD.CREATE,bill)
        }, {
            ToastUtils.showShort(it.message)
        })
    }


    fun addImgUrl(imgUrl: String) {
        imgUrls.add(imgUrl)
        imgUrlsLive.postValue(imgUrls)
    }

    private suspend fun getDealers(): MutableList<String> {
        val users = AppDatabase.getInstance().dealerDao().findAll()
        val dealerNames: MutableList<String> = ArrayList()
        users.forEach(Consumer { dealer: Dealer -> dealerNames.add(dealer.userName) })
        return dealerNames
    }

    val dealersLiveDatabase = MutableLiveData<MutableList<String>>()
        get() {
            launchIO({ field.postValue(getDealers()) }, {})
            return field
        }

    fun getBillImages(bid: String = bill.id): LiveData<MutableList<Image>> {
        return AppDatabase.getInstance().imageDao().findByBillId(bid)
            .asLiveData(viewModelScope.coroutineContext)
    }
}