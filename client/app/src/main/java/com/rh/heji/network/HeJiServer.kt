package com.rh.heji.network

import com.rh.heji.data.db.Bill
import com.rh.heji.network.request.BillEntity
import com.rh.heji.network.request.CategoryEntity
import com.rh.heji.network.response.ImageEntity
import com.rh.heji.ui.user.register.RegisterViewModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Date: 2020/9/23
 * Author: 锅得铁
 * #
 */
interface HeJiServer {
    @POST("user/register")
    fun register(@Body user: Any?): Call<BaseResponse<RegisterViewModel.User>>

    @POST("user/login")
    fun login(@Query("username") username: String, @Query("password") password: String): Call<BaseResponse<String>>

    @POST("user/auth")
    fun auth(@Query("token") token: String): Call<BaseResponse<String>>

    @POST("bill/add")
    fun saveBill(@Body entity: BillEntity): Call<BaseResponse<String>>

    @POST("bill/update")
    fun updateBill(@Body entity: BillEntity): Call<BaseResponse<String>>

    @POST("bill/addBills")
    fun saveBills(@Body entity: List<BillEntity>): Call<BaseResponse<String>>

    @GET("bill/delete")
    fun deleteBill(@Query("_id") uid: String): Call<BaseResponse<String>>

    @POST("bill/getBills")
    fun getBills(@Query("startTime") startTime: String?,
                 @Query("endTime") endTime: String?): Call<BaseResponse<List<BillEntity>>>

    @POST("bill/export")
    fun exportBills(@Query("year") year: String?, @Query("month") month: String?): Call<ResponseBody>

    @POST("category/addCategories")
    fun addCategories(@Body categories: List<CategoryEntity>): Call<BaseResponse<String>>

    @POST("category/add")
    fun addCategory(@Body categories: CategoryEntity): Call<BaseResponse<String>>

    @GET("category/delete")
    fun deleteCategoryById(@Query("_id") _id: String): Call<BaseResponse<String>>

    @GET("category/getByBookId")//获取基础的类别
    fun getCategories(@Query("book_id") book_id: String): Call<BaseResponse<List<CategoryEntity>>>

    /**
     * @param part
     * @param billId 账单ID
     * @return
     */
    @Multipart
    @POST("image/uploadImage")
    fun uploadImg(@Part part: MultipartBody.Part, @Query("billId") billId: String, @Query("time") time: Long): Call<BaseResponse<ImageEntity>>

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
}