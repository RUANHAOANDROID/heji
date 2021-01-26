package com.rh.heji.data.network

import com.rh.heji.data.network.request.BillEntity
import com.rh.heji.data.network.request.CategoryEntity
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

    @POST("bill/add")
    fun saveBill(@Body entity: BillEntity?): Call<BaseResponse<*>?>?

    @POST("bill/addBills")
    fun saveBills(@Body entity: List<BillEntity?>?): Call<BaseResponse<*>?>?

    @GET("bill/delete")
    fun deleteBill(@Query("_id") uid: String?): Call<BaseResponse<*>?>?

    @POST("bill/getBills")
    fun getBills(@Query("startTime") startTime: String?,
                 @Query("endTime") uid: String?): Call<BaseResponse<List<BillEntity?>?>?>?

    @POST("bill/export")
    fun exportBills(@Query("year") year: String?, @Query("month") month: String?): Call<ResponseBody?>?

    @POST("category/addCategories")
    fun addCategories(@Body categories: List<CategoryEntity?>?): Call<BaseResponse<*>?>?

    @POST("category/add")
    fun addCategory(@Body categories: CategoryEntity?): Call<BaseResponse<*>?>?

    @GET("category/delete")
    fun deleteCategoryById(@Query("_id") _id: String?): Call<BaseResponse<*>?>?

    @GET("category/deleteByName")
    fun deleteCategoryByName(@Query("categoryName") categoryName: String?): Call<BaseResponse<*>?>?

    @GET("category/getCategories")
    fun getCategories(@Query("type") type: Int, @Query("level") level: Int): Call<BaseResponse<List<CategoryEntity?>?>?>?

    /**
     * @param part
     * @param billId 账单ID
     * @return
     */
    @Multipart
    @POST("image/uploadImage")
    fun uploadImg(@Part part: MultipartBody.Part?, @Query("billId") billId: String?, @Query("time") time: Long): Call<BaseResponse<String?>?>?

    /**
     * 多个文件上传
     *
     * @param parts
     * @return
     */
    @Multipart
    @POST("file/uploadFiles")
    fun uploadImgs(@Part parts: List<MultipartBody.Part?>?): Call<BaseResponse<List<String?>?>?>?

    @Streaming
    @GET("file/downloadFile/{fileName}")
    fun downloadFile(@Path("fileName") fileName: String?): Call<ResponseBody?>?

    @Streaming
    @GET("image/{imageId}")
    fun getImage(@Path("imageId") _id: String?): Call<ResponseBody?>?
}