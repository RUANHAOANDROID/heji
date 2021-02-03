package com.rh.heji.network

import com.rh.heji.AppCache
import com.rh.heji.network.request.BillEntity
import com.rh.heji.network.request.CategoryEntity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HejiNetwork {
    private val hejiServer = AppCache.instance.heJiServer

    suspend fun login(username: String, password: String) = hejiServer.login(username, password).await()

    suspend fun billPush(billEntity: BillEntity) = hejiServer.saveBill(billEntity).await()
    suspend fun billDelete(_id: String) = hejiServer.deleteBill(_id).await()
    suspend fun billUpdate(billEntity: BillEntity) = hejiServer.updateBill(billEntity).await()
    suspend fun billPull(startTime: String, endTime: String) = hejiServer.getBills(startTime, endTime).await()
    suspend fun billImageUpload(@Part part: MultipartBody.Part, _id: String, bill_id: String, time: Long) = hejiServer.uploadImg(part, _id, bill_id, time).await()
    suspend fun billPullImages(_id: String) = hejiServer.getBillImages(_id).await()

    suspend fun categoryPush(category: CategoryEntity) = hejiServer.addCategory(category).await()
    suspend fun categoryDelete(_id: String) = hejiServer.deleteCategoryById(_id).await()
    suspend fun categoryPull(_id: String = "0") = hejiServer.getCategories(_id).await()


    private suspend fun <T> Call<T>.await(): T {
        //调用suspendCoroutine 挂起
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)//异常恢复
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    val errorBody = response.errorBody()
                    when {
                        body != null -> continuation.resume(body)//正常恢复
                        errorBody != null -> continuation.resumeWithException(RuntimeException(errorBody.string()))
                        else -> continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }
            })
        }
    }

    companion object {

        private var network: HejiNetwork? = null

        fun getInstance(): HejiNetwork {
            if (network == null) {
                synchronized(HejiNetwork::class.java) {
                    if (network == null) {
                        network = HejiNetwork()
                    }
                }
            }
            return network!!
        }

    }

}