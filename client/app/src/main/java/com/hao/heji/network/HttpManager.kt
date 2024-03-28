package com.hao.heji.network

import com.hao.heji.BuildConfig
import com.hao.heji.config.Config
import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.Book
import com.hao.heji.network.request.CategoryEntity
import com.hao.heji.ui.user.register.RegisterUser
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HttpManager {
    private val hejiServer = HttpRetrofit.create(BuildConfig.HTTP_URL, ApiServer::class.java)

    suspend fun register(registerUser: RegisterUser) = hejiServer.register(registerUser).await()
    suspend fun login(username: String, password: String) =
        hejiServer.login(mapOf("tel" to username, "password" to password)).await()

    suspend fun book(book_id: String) = hejiServer.findBook(book_id).await()
    suspend fun createBook(book: Book) = hejiServer.createBook(book).await()
    suspend fun bookList() = hejiServer.bookList().await()
    suspend fun bookShared(book_id: String) = hejiServer.sharedBook(book_id).await()
    suspend fun bookDelete(book_id: String) = hejiServer.deleteBook(book_id).await()
    suspend fun bookUpdate(book_id: String, bookName: String, bookType: String) =
        hejiServer.updateBook(book_id, bookName, bookType).await()

    suspend fun joinBook(sharedCode: String) = hejiServer.joinBook(sharedCode).await()
    suspend fun billPush(bill: Bill) =
        hejiServer.saveBill(bill.apply { images = mutableListOf() }).await()

    suspend fun billDelete(_id: String) = hejiServer.deleteBill(_id).await()
    suspend fun billUpdate(bill: Bill) = hejiServer.updateBill(bill).await()
    suspend fun billPull(startTime: String, endTime: String, book_id: String = Config.book.id) =
        hejiServer.getBills(book_id, startTime, endTime).await()

    suspend fun imageUpload(
        @Part part: MultipartBody.Part,
        _id: String,
        _bid: String,
        time: Long,
    ) = hejiServer.uploadImg(part, _id, _bid, time).await()

    suspend fun imageDownload(_id: String) = hejiServer.getBillImages(_id).await()
    suspend fun imageDelete(billId: String, imageId: String) =
        hejiServer.imageDelete(billId, imageId).await()

    suspend fun billExport(year: String = "0", month: String = "0"): Response<ResponseBody> =
        hejiServer.exportBills(year, month).execute()

    suspend fun categoryPush(category: CategoryEntity) = hejiServer.addCategory(category).await()
    suspend fun categoryDelete(_id: String) = hejiServer.deleteCategoryById(_id).await()
    suspend fun categoryPull(_id: String = "0") = hejiServer.getCategories(_id).await()
    suspend fun categoryUpdate(_id: String = "0") = hejiServer.getCategories(_id).await()


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
                        errorBody != null -> {//error body 仅仅适用于服务器统一返回的错误格式，在服务端错误信息同样返回{code,msg,data}格式
                            continuation.resumeWithException(RuntimeException(errorBody.string()))
                        }
                        else -> {
                            continuation.resumeWithException(RuntimeException("response body is null"))
                        }
                    }
                }
            })
        }
    }


    companion object {

        private var network: HttpManager? = null

        fun getInstance(): HttpManager {
            if (network == null) {
                synchronized(HttpManager::class.java) {
                    if (network == null) {
                        network = HttpManager()
                    }
                }
            }
            return network!!
        }

    }

}
