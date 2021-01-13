package com.rh.heji.data.network;

import com.rh.heji.data.network.request.BillEntity;
import com.rh.heji.data.network.request.CategoryEntity;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Date: 2020/9/23
 * Author: 锅得铁
 * #
 */

public interface HeJiServer {

    @POST("bill/add")
    Call<BaseResponse> saveBill(@Body BillEntity entity);

    @POST("bill/addBills")
    Call<BaseResponse> saveBills(@Body List<BillEntity> entity);

    @GET("bill/delete")
    Call<BaseResponse> deleteBill(@Query("_id") String uid);

    @POST("bill/getBills")
    Call<BaseResponse<List<BillEntity>>> getBills(@Query("startTime") String startTime,
                                                  @Query("endTime") String uid,
                                                  @Query("type") int type);

    @POST("bill/export")
    Call<ResponseBody> exportBills(@Query("year") String year, @Query("month") String month);

    @POST("category/add")
    Call<BaseResponse> addCategory(@Body List<CategoryEntity> categories);

    @POST("category/addCategories")
    Call<BaseResponse> addCategory(@Body CategoryEntity categories);

    @GET("category/deleteCategory")
    Call<BaseResponse> deleteCategory(@Query("categoryName") String categoryName);


    @GET("category/getCategories")
    Call<BaseResponse<List<CategoryEntity>>> getCategories(@Query("type") int type, @Query("level") int level);

    /**
     * @param part
     * @param billId 账单ID
     * @return
     */
    @Multipart
    @POST("image/uploadImage")
    Call<BaseResponse<String>> uploadImg(@Part() MultipartBody.Part part, @Query("billId") String billId);

    /**
     * 多个文件上传
     *
     * @param parts
     * @return
     */
    @Multipart
    @POST("file/uploadFiles")
    Call<BaseResponse<List<String>>> uploadImgs(@Part() List<MultipartBody.Part> parts);

    @Streaming
    @GET("file/downloadFile/{fileName}")
    Call<ResponseBody> downloadFile(@Path("fileName") String fileName);

}
