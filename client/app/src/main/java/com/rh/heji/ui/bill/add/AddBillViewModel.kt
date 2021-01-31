package com.rh.heji.ui.bill.add

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Constant
import com.rh.heji.data.db.Dealer
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.BillEntity
import com.rh.heji.ui.base.BaseViewModel
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
    private var imgUrls: MutableList<String> = ArrayList() //image list
    private val imgUrlsLive = MutableLiveData<List<String>>() // image live
    private val billType = BillType.EXPENDITURE
    val bill = Bill()
    var time: String? = null
        get() {
            field
            if (TextUtils.isEmpty(field)) field = TimeUtils.getNowString()
            return field
        }
    var keyBoardStack: Stack<String>? = null
    var saveLiveData: MutableLiveData<Bill>? = null
    private val billRepository = BillRepository()

    /**
     * @param billId
     * @param money
     * @param billType
     * @return
     */
    fun save(billId: String?, money: String?, billType: BillType): LiveData<Bill> {
        val bill = bill
        saveLiveData = MutableLiveData<Bill>()
        val billTime = TimeUtils.string2Millis(time, "yyyy-MM-dd HH:mm")//String time to millis
        LogUtils.d(TimeUtils.millis2String(billTime, "yyyy-MM-dd HH:mm"))
        bill.setId(billId!!)
        bill.setMoney(BigDecimal(money))
        bill.setCreateTime(System.currentTimeMillis())
        bill.billTime = billTime
        //
        val images = imgUrls.stream().map { s: String? ->
            val image = Image(ObjectId().toString(),billId)
            image.localPath = s
            image
        }.collect(Collectors.toList())
        bill.imgCount = images.size
        if (bill.getCategory() == null) {
            bill.setType(billType.type())
            bill.setCategory(billType.text())
        }
        launch({
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance().imageDao().install(images)
                val count = AppDatabase.getInstance().billDao().install(bill)
                if (count > 0) {
                    ToastUtils.showShort("$count: 保存成功")
                }
                saveLiveData?.postValue(bill)
            }
        }, {
            ToastUtils.showShort(it.message)
        })
        return saveLiveData!!
    }

    fun addImgUrl(imgUrl: String) {
        imgUrls.add(imgUrl)
        imgUrlsLive.postValue(imgUrls)
    }

    fun setImgUrls(imgUrls: MutableList<String>) {
        this.imgUrls = imgUrls
        imgUrlsLive.postValue(imgUrls)
    }

    fun removeImgUrl(imgUrl: String?) {
        if (imgUrls.size > 0 && imgUrls.contains(imgUrl)) {
            imgUrls.remove(imgUrl)
        }
        imgUrlsLive.postValue(imgUrls)
    }

    fun getImgUrlsLive(): MutableLiveData<List<String>> {
        imgUrlsLive.postValue(imgUrls)
        return imgUrlsLive
    }

    fun getImgUrls(): List<String> {
        return imgUrls
    }

    val dealers: List<String>
        get() {
            val users = AppDatabase.getInstance().dealerDao().findAll()
            val dealerNames: MutableList<String> = ArrayList()
            users.forEach(Consumer { dealer: Dealer -> dealerNames.add(dealer.userName) })
            return dealerNames
        }

}