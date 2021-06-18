package com.rh.heji.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Date: 2020/10/27
 * Author: 锅得铁
 * #
 */
interface ETCServer {
    @GET("/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacount/{number}/{month}/{carNumber}")
    fun queryETCByMonth(
        @Path(value = "number") etcNumber: String?,
        @Path(value = "month") yymm: String?,
        @Path(value = "carNumber") carNumber: String?
    ): Call<ResponseBody?>?

    @GET("/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacountdetail/{etcID}/{yymm}/{carID}")
    fun queryETCListDetail(
        @Path(value = "etcID") etcNumber: String?,
        @Path(value = "yymm") yymm: String?,
        @Path(value = "carID") carNumber: String?
    ): Call<ResponseBody?>?

    @Headers(
        "content-type: application/x-www-form-urlencoded; charset=UTF-8",
        "Accept: application/json, text/javascript, */*; q=0.01",
        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0",
        "X-Requested-With: XMLHttpRequest"
    )
    @POST("/HuBeiCityAPIServer/index.php/huibeicityserver/loadmonthinfo")
    fun queryETCInfoList(@Body str: String?): Call<ResponseBody?>?

    companion object {
        const val ETC_BASE_URL = "http://hubeiweixin.u-road.com"
        const val ETC_URL =
            "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacount/" +
                    "42021909230571219224/" +
                    "202008/" +
                    "%E9%84%82FNA518"
        const val ETC_LIST =
            "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacountdetail/42021909230571219224/2020-09/%E9%84%82FNA518"
        const val ETC_INFO_LIST = "http://hubeiweixin.u-road.com:80"
    }
}