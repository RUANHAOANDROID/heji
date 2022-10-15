package com.rh.heji.ui.setting.input.etc

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.ETC
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.STATUS
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.moshi
import com.rh.heji.service.sync.IBillSync
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.setting.input.etc.dto.ETCListInfoEntity
import com.rh.heji.ui.setting.input.etc.dto.HBETCEntity
import com.rh.heji.ui.setting.input.etc.dto.HBETCEntity.DataBean.OrderArrBean
import com.rh.heji.utlis.http.basic.HttpRetrofit
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer

/**
 * Date: 2020/11/10
 * @author: 锅得铁
 * #
 */
internal class ETCViewModel(private val mBillSync: IBillSync) : BaseViewModel<ETCAction, ETCUiState>() {


    var etcID = ETC.ID
    var carID = ETC.CAR_ID
    var yearMonth: String? = null

    /**
     * 获取类别
     *
     * @return 类别名称
     */
    private val categoryName: String
        get() {
            val categories = App.dataBase.categoryDao().queryByCategoryName("过路费")
            val category: Category
            if (categories.isEmpty()) {
                category = Category(name = "过路费", bookId = App.currentBook.id).apply {
                    level = 0
                    type = BillType.EXPENDITURE.type()
                }
                category.synced = STATUS.NOT_SYNCED
                App.dataBase.categoryDao().insert(category)
            } else {
                category = categories[0]
            }
            return category.name
        }

    override fun doAction(action: ETCAction) {
        super.doAction(action)
        if (action is ETCAction.RequestETCBill) {
            requestHBGSETCList(action.etcID, action.month, action.carID)
        }
    }

    /**
     * 保存到数据库
     *
     * @param strBody 内容
     * @return
     */
    private fun saveToDB(strBody: String) {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(DateConverters)
            .add(MoneyConverters)
            .build()
        val jsonAdapter: JsonAdapter<ETCListInfoEntity> = moshi.adapter(
            ETCListInfoEntity::class.java
        )
        val etcListInfo = jsonAdapter.fromJson(strBody)
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
                val bills = App.dataBase.billDao().findIds(bill.billTime, bill.money, bill.remark!!)
                if (bills.size <= 0) {
                    val count = App.dataBase.billDao().install(bill)
                    LogUtils.d("导入ETC账单：", bill)
                    if (count > 0)
                        mBillSync.add(bill)
                } else {
                    LogUtils.d("ETC账单已存在", bills)
                }
            })
            send(ETCUiState.InputSuccess)

        } else {
            send(ETCUiState.InputError(RuntimeException("input fail :null data ")))
        }
    }

    /**
     * 请求账单详情列表
     *
     * @param etcID ETC号码
     * @param month 月份
     * @param carID 车牌号
     */
    private fun requestHBGSETCList(etcID: String, month: String, carID: String) {
        val requestURL = "http://www.hbgsetc.com/index.php?/newhome/getMonthBillData"
        //www - url 解码方式
        val requestBody =
            "cardNo=$etcID&month=$month&vehplate=$carID&flag=0".toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        //伪装成浏览器请求
        val request: Request = Request.Builder()
            .url(requestURL)
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .addHeader("Accept", "*/*")
            .addHeader("User-Agent", ETC.USER_AGENTS[Random().nextInt(ETC.USER_AGENTS.size - 1)])
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .post(requestBody)
            .build()
        HttpRetrofit.okHttpClient().newCall(request).enqueue(object : Callback {
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
                                val jsonAdapter: JsonAdapter<HBETCEntity> = moshi.adapter(
                                    HBETCEntity::class.java
                                )
                                val hbetcEntity = jsonAdapter.fromJson(strBody)
                                if (hbetcEntity?.data != null && hbetcEntity.data.orderArr.size > 0) {
                                    val data = hbetcEntity.data.orderArr
                                    data.forEach(Consumer { info: OrderArrBean -> saveToBillDB(info) })
                                    send(ETCUiState.InputSuccess)
                                } else {
                                    ToastUtils.showShort("导入失败")
                                    send(ETCUiState.InputError(RuntimeException("导入失败")))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            ToastUtils.showShort("解析失败")
                            send(ETCUiState.InputError(RuntimeException("解析错误")))
                        }
                    }
                } else if (response.code == 404) {
                    requestETCList2(etcID, month, carID)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                ToastUtils.showLong(e.message)
                send(ETCUiState.InputError(RuntimeException("请求错误")))
                requestETCList2(etcID, month, carID)
            }
        })
    }

    /**
     *
     * 请求1失败后尝试2
     * 请求账单详情列表
     *
     * @param etcID ETC号码
     * @param month 月份
     * @param carID 车牌号
     */
    private fun requestETCList2(etcID: String, month: String, carID: String) {
        //伪装User-Agent
        val url =
            "http://hubeiweixin.u-road.com:80/HuBeiCityAPIServer/index.php/huibeicityserver/loadmonthinfo"
        LogUtils.d("尝试URL2", url)
        //www - url 解码方式
        val requestBody =
            "caidno=$etcID&month=$month&vehplate=$carID".toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        //伪装成浏览器请求
        val request: Request = Request.Builder()
            .url(url)
            .addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
            .addHeader("User-Agent", ETC.USER_AGENTS[Random().nextInt(ETC.USER_AGENTS.size - 1)])
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .post(requestBody)
            .build()
        HttpRetrofit.okHttpClient().newCall(request).enqueue(object : Callback {
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
                            send(ETCUiState.InputError(RuntimeException("解析错误")))
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                ToastUtils.showLong(e.message)
                send(ETCUiState.InputError(RuntimeException("请求错误")))
            }
        })
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
        val bills = App.dataBase.billDao().findIds(
            bill.billTime, bill.money,
            bill.remark!!
        )
        if (bills.size <= 0) {
            val count = App.dataBase.billDao().install(bill)
            LogUtils.d("导入ETC账单：", bill)
            if (count > 0)
                mBillSync.add(bill)
        } else {
            LogUtils.d("ETC账单已存在", bills)
        }
    }
}