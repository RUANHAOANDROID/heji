package com.rh.heji.network

import com.rh.heji.BuildConfig
import com.rh.heji.App.Companion.currentBook
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.ErrorLog
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.ui.user.register.RegisterUser
import com.rh.heji.utlis.http.basic.HttpRetrofit
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.HTTP
import retrofit2.http.Part
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HejiNetwork {
    private val hejiServer = HttpRetrofit.create(BuildConfig.HTTP_URL, HeJiServer::class.java)

    suspend fun register(registerUser: RegisterUser) = hejiServer.register(registerUser).await()
    suspend fun login(username: String, password: String) =
        hejiServer.login(username, password).await()

    suspend fun auth(token: String) = hejiServer.auth(token).await()

    suspend fun bookOperateLogs(book_id: String) = hejiServer.bookOperateLogs(book_id).await()
    suspend fun book(book_id: String) = hejiServer.bookFind(book_id).await()
    suspend fun bookCreate(book: Book) = hejiServer.bookCreate(book).await()
    suspend fun bookPull() = hejiServer.bookGet().await()
    suspend fun bookGetUsers(book_id: String) = hejiServer.bookGetBookUsers(book_id).await()
    suspend fun bookShared(book_id: String) = hejiServer.bookShared(book_id).await()
    suspend fun bookDelete(book_id: String) = hejiServer.bookDelete(book_id).await()
    suspend fun bookUpdate(book_id: String, bookName: String, bookType: String) =
        hejiServer.bookUpdate(book_id, bookName, bookType).await()

    suspend fun bookJoin(sharedCode: String) = hejiServer.bookJoin(sharedCode).await()
    suspend fun billPush(bill: Bill) = hejiServer.saveBill(bill).await()
    suspend fun billDelete(_id: String) = hejiServer.deleteBill(_id).await()
    suspend fun billUpdate(bill: Bill) = hejiServer.updateBill(bill).await()
    suspend fun billPull(startTime: String, endTime: String, book_id: String = currentBook!!.id) =
        hejiServer.getBills(book_id, startTime, endTime).await()

    suspend fun imageUpload(
        @Part part: MultipartBody.Part,
        _id: String,
        _bid: String,
        time: Long,
    ) = hejiServer.uploadImg(part, _id, _bid, time).await()

    suspend fun imageDownload(_id: String) = hejiServer.getBillImages(_id).await()
    suspend fun billExport(year: String = "0", month: String = "0"): Response<ResponseBody> =
        hejiServer.exportBills(year, month).execute()

    suspend fun categoryPush(category: CategoryEntity) = hejiServer.addCategory(category).await()
    suspend fun categoryDelete(_id: String) = hejiServer.deleteCategoryById(_id).await()
    suspend fun categoryPull(_id: String = "0") = hejiServer.getCategories(_id).await()
    suspend fun categoryUpdate(_id: String = "0") = hejiServer.getCategories(_id).await()
    suspend fun logUpload(errorLog: ErrorLog) = hejiServer.logUpload(errorLog).await()


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
                        body != null && response.code() == 200 -> {
                            continuation.resume(body)//正常恢复
                        }
                        errorBody != null -> {//error body 仅仅适用于服务器统一返回的错误格式，在服务端错误信息同样返回{code,message,data}格式
                            handleError(errorBody, response.code())
                        }
                        else -> {
                            continuation.resumeWithException(RuntimeException("response body is null"))
                        }
                    }
                }

                private fun handleError(errorBody: ResponseBody, code: Int) {
                    when (code) {
                        401 -> {
                            continuation.resumeWithException(RuntimeException("密码错误"))
                        }
                        500 -> {
                            val errorMsg: String =
                                JSONObject(errorBody.string()).optString("msg").toString()
                            if (errorMsg.isNotEmpty()) {
                                continuation.resumeWithException(RuntimeException(errorMsg))
                            } else {
                                continuation.resumeWithException(RuntimeException(errorBody.string()))
                            }
                        }
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
