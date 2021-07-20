package com.rh.heji.ui.setting.input.etc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.rh.heji.AppCache
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.db.STATUS.NOT_SYNCED
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.setting.input.etc.HBETCEntity.DataBean.OrderArrBean
import com.rh.heji.utlis.http.basic.OkHttpConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer

/**
 * Date: 2020/11/10
 * Author: 锅得铁
 * #
 */
class ETCViewModel : BaseViewModel() {
    var etcID = DEF_ETC_ID
    var carID = DEF_CAR_ID
    var yearMonth: String? = null
    var etcLive: MediatorLiveData<String> = MediatorLiveData()

    /**
     * 获取类别
     *
     * @return 类别名称
     */
    private val categoryName: String
         get() {
            val categories =   AppDatabase.getInstance().categoryDao().queryByCategoryName("过路费")
            val category: Category
            if (categories.isEmpty()) {
                category = Category(category = "过路费", level = 0, type = BillType.EXPENDITURE.type())
                category.synced = STATUS.NOT_SYNCED
                  AppDatabase.getInstance().categoryDao().insert(category)
            } else {
                category = categories[0]
            }
            return category.category
        }

    /**
     * 保存到数据库
     *
     * @param strBody 内容
     * @return
     */
    private fun saveToDB(strBody: String) {
        val gson = Gson()
        val etcListInfo = gson.fromJson(strBody, ETCListInfoEntity::class.java)
        if (etcListInfo?.data != null && etcListInfo.data.size > 0) {
            val data = etcListInfo.data
            data.forEach(Consumer { info: ETCListInfoEntity.Info ->
                val billTime = TimeUtils.string2Date(info.exchargetime, "yyyy-MM-dd HH:mm:ss")
                val bill = Bill()
                bill.id = ObjectId(billTime).toString()
                bill.money = BigDecimal(info.etcPrice).divide(BigDecimal(100))
                bill.remark = info.exEnStationName
                bill.billTime = billTime
                bill.category = categoryName
                bill.dealer = "ETC"
                bill.createTime = TimeUtils.getNowMills()
                bill.type = BillType.EXPENDITURE.type()
                /**
                 * 如果不存在才插入
                 */
                /**
                 * 如果不存在才插入
                 */
                val bills =   AppDatabase.getInstance().billDao().findIds(bill.billTime!!, bill.money, bill.remark!!)
                if (bills.size <= 0) {
                      AppDatabase.getInstance().billDao().install(bill)
                    LogUtils.d("导入ETC账单：", bill)
                } else {
                    LogUtils.d("ETC账单已存在", bills)
                }
            })
            etcLive.postValue("导入完成")
            AppCache.getInstance().appViewModule.asyncData()
        } else {
            ToastUtils.showShort("导入失败")
            etcLive.postValue("导入失败")
        }
    }

    /**
     * 请求账单详情列表
     *
     * @param etcID ETC号码
     * @param month 月份
     * @param carID 车牌号
     */
    fun requestHBGSETCList(etcID: String, month: String, carID: String): LiveData<String> {
        val requestURL = "http://www.hbgsetc.com/index.php?/newhome/getMonthBillData"
        //www - url 解码方式
        val requestBody = "cardNo=$etcID&month=$month&vehplate=$carID&flag=0".toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        //伪装成浏览器请求
        val request: Request = Request.Builder()
                .url(requestURL)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .addHeader("User-Agent", USER_AGENTS[Random().nextInt(USER_AGENTS.size - 1)])
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .post(requestBody)
                .build()
        OkHttpConfig.clientBuilder.build().newCall(request).enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    if (response.body != null) {
                        val strBody = response.body!!.string()
                        try {
                            val jsonObject = JSONObject(strBody)
                            val status = jsonObject.getString("status")
                            if (status == "error") {
                                val error = jsonObject.getString("msg")
                                ToastUtils.showLong(error)
                            } else if (status == "OK") {
                                val gson = Gson()
                                val hbetcEntity = gson.fromJson(strBody, HBETCEntity::class.java)
                                if (hbetcEntity?.data != null && hbetcEntity.data.orderArr.size > 0) {
                                    val data = hbetcEntity.data.orderArr
                                    data.forEach(Consumer { info: OrderArrBean -> saveToBillDB(info) })
                                    etcLive.postValue("导入完成")
                                } else {
                                    ToastUtils.showShort("导入失败")
                                    etcLive.postValue("导入失败")
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            ToastUtils.showShort("解析失败")
                            etcLive.postValue("解析错误")
                        }
                    }
                } else if (response.code == 404) {
                    requestETCList2(etcID, month, carID)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                ToastUtils.showLong(e.message)
                etcLive.postValue("请求错误")
                requestETCList2(etcID, month, carID)
            }
        })
        return etcLive
    }

    /**
     * 请求账单详情列表
     *
     * @param etcID ETC号码
     * @param month 月份
     * @param carID 车牌号
     */
    fun requestETCList2(etcID: String, month: String, carID: String): LiveData<String?> {
        //伪装User-Agent
        val url = "http://hubeiweixin.u-road.com:80/HuBeiCityAPIServer/index.php/huibeicityserver/loadmonthinfo"
        LogUtils.d("尝试URL2", url)
        //www - url 解码方式
        val requestBody = "caidno=$etcID&month=$month&vehplate=$carID".toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        //伪装成浏览器请求
        val request: Request = Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .addHeader("User-Agent", USER_AGENTS[Random().nextInt(USER_AGENTS.size - 1)])
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .post(requestBody)
                .build()
        OkHttpConfig.clientBuilder.build().newCall(request).enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    if (response.body != null) {
                        val strBody = response.body!!.string()
                        try {
                            val jsonObject = JSONObject(strBody)
                            val status = jsonObject.getString("status")
                            if (status == "error") {
                                val error = jsonObject.getString("msg")
                                ToastUtils.showLong(error)
                            } else {
                                saveToDB(strBody)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            ToastUtils.showShort("解析失败")
                            etcLive.postValue("解析错误")
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                ToastUtils.showLong(e.message)
                etcLive.postValue("请求错误")
            }
        })
        return etcLive
    }

    private fun saveToBillDB(info: OrderArrBean) {
        val money = info.totalFee
        val remark = info.enStationName + "|" + info.exStationName
        val billTime = DateConverters.str2Date(info.exTime)
        val bill = Bill()
        bill.id = ObjectId(billTime).toString()
        bill.money = BigDecimal(money).divide(BigDecimal(100))
        bill.remark = remark
        bill.billTime = billTime
        bill.category = categoryName
        bill.dealer = "ETC"
        bill.createTime = TimeUtils.getNowMills()
        bill.type = BillType.EXPENDITURE.type()
        /**
         * 如果不存在才插入(插入时必须保持格式一致)
         */
        val bills =   AppDatabase.getInstance().billDao().findIds(bill.billTime!!, bill.money,
            bill.remark!!
        )
        if (bills.size <= 0) {
              AppDatabase.getInstance().billDao().install(bill)
            LogUtils.d("导入ETC账单：", bill)
        } else {
            LogUtils.d("ETC账单已存在", bills)
        }
    }

    companion object {
        const val ETC_URL = "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcsearch"
        const val DEF_ETC_ID = "42021909230571219224"
        const val DEF_CAR_ID = "鄂FNA518"

        //伪装User-Agent
        val USER_AGENTS = arrayOf( //"Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0",
                "Mozilla/5.0 (Linux; Android 10; MI 8 Lite Build/QKQ1.190910.002; wv) ",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
    }
}