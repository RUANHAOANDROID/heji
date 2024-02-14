package com.rh.heji.network

import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.BookUser
import com.rh.heji.data.db.ErrorLog
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.network.response.ImageEntity
import com.rh.heji.network.response.OperateLog
import com.rh.heji.ui.user.register.RegisterUser
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @date: 2020/9/23
 * @author: 锅得铁
 * #
 */
interface HeJiServer {
    //----------------------USER---------------------------//
    @POST("/api/v1/Register")
    fun register(@Body user: Any?): Call<BaseResponse<String>>

    @POST("/api/v1/Login")
    fun login(@Body map: Map<String,String>): Call<BaseResponse<String>>

    @POST("user/auth")
    fun auth(@Header("token") token: String): Call<BaseResponse<String>>

    @GET("operateLog/bookLogs")
    fun bookOperateLogs(@Query("bookId") bookId: String): Call<BaseResponse<MutableList<OperateLog>>>

    //----------------------BOOK---------------------------//
    @POST("book/create")
    fun bookCreate(@Body book: Book): Call<BaseResponse<String>>

    @POST("book/findBook")
    fun bookFind(@Query("bookId") bookId: String): Call<BaseResponse<Book>>

    @POST("book/shared")
    fun bookShared(@Query("bookId") bookId: String): Call<BaseResponse<String>>

    @POST("book/join")
    fun bookJoin(@Query("sharedCode") sharedCode: String): Call<BaseResponse<String>>

    @POST("book/removeBookUser")
    fun bookRemoveUser(@Body book: Book): Call<BaseResponse<String>>

    @POST("book/updateBook")
    fun bookUpdate(@Query("bookId")  bookId:String,
                   @Query("bookName") bookName:String,
                   @Query("bookType")  bookType:String):Call<BaseResponse<String>>

    @DELETE("book/deleteBook")
    fun bookDelete(@Query("bookId") book: String): Call<BaseResponse<String>>

    @POST("book/getBooks")
    fun bookGet(): Call<BaseResponse<MutableList<Book>>>

    @POST("book/getBookUsers")
    fun bookGetBookUsers(@Query("bookId") bookId: String): Call<BaseResponse<MutableList<BookUser>>>

    //----------------------BILL---------------------------//
    @POST("bill/add")
    fun saveBill(@Body entity: Bill): Call<BaseResponse<String>>

    @POST("bill/addBills")
    fun saveBill(@Header("book_id") book_id:String, @Body entity: List<Bill>): Call<BaseResponse<String>>

    @POST("bill/update")
    fun updateBill(@Body entity: Bill): Call<BaseResponse<String>>

    @POST("bill/addBills")
    fun saveBills(@Body entity: List<Bill>): Call<BaseResponse<String>>

    @DELETE("bill/delete")
    fun deleteBill(@Query("_id") uid: String): Call<BaseResponse<String>>

    @POST("bill/getBills")
    fun getBills(@Query("book_id")book_id: String,@Query("startTime") startTime: String?,
                 @Query("endTime") endTime: String?): Call<BaseResponse<List<Bill>>>

    @POST("bill/export")
    fun exportBills(@Query("year") year: String?, @Query("month") month: String?): Call<ResponseBody>
    //----------------------CATEGORY---------------------------//
    @POST("category/addCategories")
    fun addCategories(@Body categories: List<CategoryEntity>): Call<BaseResponse<String>>

    @POST("category/add")
    fun addCategory(@Body categories: CategoryEntity): Call<BaseResponse<String>>

    @DELETE("category/delete")
    fun deleteCategoryById(@Query("_id") _id: String): Call<BaseResponse<String>>

    @GET("category/getByBookId")//获取基础的类别
    fun getCategories(@Query("book_id") book_id: String): Call<BaseResponse<List<CategoryEntity>>>

    @GET("category/update")//获取基础的类别
    fun categoryUpdate(@Query("book_id") book_id: String): Call<BaseResponse<List<CategoryEntity>>>
    //----------------------FILE IMAGE---------------------------//
    /**
     * @param part
     * @param billId 账单ID
     * @return
     */
    @Multipart
    @POST("image/uploadImage")
    fun uploadImg(@Part part: MultipartBody.Part,
                  @Query("_id") _id: String,
                  @Query("billId") billId: String,
                  @Query("time") time: Long): Call<BaseResponse<ImageEntity>>

    /**
     * 多个文件上传
     *
     * @param parts
     * @return
     */
    @Multipart
    @POST("image/uploadFiles")
    fun uploadImgs(@Part parts: List<MultipartBody.Part>): Call<BaseResponse<List<String>>>

    @Streaming
    @GET("image/downloadFile/{fileName}")
    fun downloadFile(@Path("fileName") fileName: String): Call<ResponseBody>

    @Streaming
    @GET("image/getBillImages")
    fun getBillImages(@Query("bill_id") bill_id: String): Call<BaseResponse<List<ImageEntity>>>

    @Streaming
    @GET("image/{imageId}")
    fun getImage(@Path("imageId") _id: String): Call<ResponseBody>

    @Streaming
    @POST("image/delete")
    fun imageDelete(@Query("billId") billId: String,@Query("imageId") imageId :String): Call<BaseResponse<String>>

    //----------------------LOG FILE---------------------------//
    @POST("log/upload")
    fun logUpload(@Body errorLog: ErrorLog):Call<BaseResponse<String>>
}