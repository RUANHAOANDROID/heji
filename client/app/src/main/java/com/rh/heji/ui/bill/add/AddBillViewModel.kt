package com.rh.heji.ui.bill.add

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.currentBook
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * 账单添加页ViewModel 不要在其他页面应用该ViewModel
 */
class AddBillViewModel : BaseViewModel() {
    var imgUrls = mutableListOf<String>()
        set(value) {
            field = value
            imgUrlsLive.postValue(imgUrls)
        }//image list
    var imgUrlsLive = MutableLiveData<List<String>>() // image live

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
    fun save(money: String, category: Category, observer: Observer<Bill>) {
        val images = imgUrls.stream().map { s: String? ->
            val image = Image(ObjectId().toString(), bill.id)
            image.localPath = s
            image
        }.collect(Collectors.toList())
        bill.apply {
            bookId = currentBook.id
            //id = billId
            this.money = BigDecimal(money)
            createTime = System.currentTimeMillis()
            //Image count
            bill.imgCount = images.size
            bill.type = category.type
            bill.category = category.category
        }
        launchIO({
            val count = AppDatabase.getInstance().billDao().install(bill)
            AppDatabase.getInstance().imageDao().install(images)
            bill.imgCount = images.size
            AppDatabase.getInstance().billDao().update(bill)
            withContext(Dispatchers.Main) {
                observer.onChanged(bill)
            }
            if (count > 0) {
                ToastUtils.showShort("已保存: ${bill.category + money}  ")
            }
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