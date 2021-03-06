package com.rh.heji.ui.bill.add

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.CURRENT_BOOK
import com.rh.heji.CURRENT_BOOK_ID
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO
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

    val bill = Bill()
    var time: String? = null
        get() {
            if (TextUtils.isEmpty(field)) field = TimeUtils.getNowString()
            return field
        }
    var keyBoardStack: Stack<String>? = null
    private var saveLiveData: MutableLiveData<Bill> = MutableLiveData()

    /**
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    fun save(billId: String?, money: String?, category: Category): MutableLiveData<Bill> {
        val bill = bill
        bill.bookId =AppCache.getInstance().currentBook.id
        bill.id = billId!!
        bill.money = BigDecimal(money)
        bill.createTime = System.currentTimeMillis()
        bill.billTime = TimeUtils.string2Date(time)
        //
        val images = imgUrls.stream().map { s: String? ->
            val image = Image(ObjectId().toString(), billId)
            image.localPath = s
            image
        }.collect(Collectors.toList())
        bill.imgCount = images.size
        bill.type = category.type
        bill.category = category.category
        launchIO({
            val count =   AppDatabase.getInstance().billDao().install(bill)
              AppDatabase.getInstance().imageDao().install(images)
            bill.imgCount =images.size
              AppDatabase.getInstance().billDao().update(bill)
            saveLiveData.postValue(bill)
            if (count > 0) {
                ToastUtils.showShort("已保存: ${bill.category + money}  ")
            }
        }, {
            ToastUtils.showShort(it.message)
        })
        return saveLiveData
    }

    fun addImgUrl(imgUrl: String) {
        imgUrls.add(imgUrl)
        imgUrlsLive.postValue(imgUrls)
    }

    private suspend fun getDealers(): MutableList<String> {
        val users =   AppDatabase.getInstance().dealerDao().findAll()
        val dealerNames: MutableList<String> = ArrayList()
        users.forEach(Consumer { dealer: Dealer -> dealerNames.add(dealer.userName) })
        return dealerNames
    }

    val dealersLiveDatabase = MutableLiveData<MutableList<String>>()
        get() {
            launchIO({ field.postValue(getDealers()) }, {})
            return field
        }

}