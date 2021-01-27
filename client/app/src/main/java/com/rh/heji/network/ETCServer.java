package com.rh.heji.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Date: 2020/10/27
 * Author: 锅得铁
 * #
 */
public interface ETCServer {
    public static final String ETC_BASE_URL = "http://hubeiweixin.u-road.com";
    public static final String ETC_URL = "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacount/" +
            "42021909230571219224/" +
            "202008/" +
            "%E9%84%82FNA518";
    public static final String ETC_LIST = "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacountdetail/42021909230571219224/2020-09/%E9%84%82FNA518";

    @GET("/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacount/{number}/{month}/{carNumber}")
    Call<ResponseBody> queryETCByMonth(@Path(value = "number") String etcNumber,
                                       @Path(value = "month") String yymm,
                                       @Path(value = "carNumber") String carNumber);

    @GET("/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacountdetail/{etcID}/{yymm}/{carID}")
    Call<ResponseBody> queryETCListDetail(@Path(value = "etcID") String etcNumber,
                                          @Path(value = "yymm") String yymm,
                                          @Path(value = "carID") String carNumber);

    public static final String ETC_INFO_LIST = "http://hubeiweixin.u-road.com:80";

    @Headers({
            "content-type: application/x-www-form-urlencoded; charset=UTF-8",
            "Accept: application/json, text/javascript, */*; q=0.01",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0",
            "X-Requested-With: XMLHttpRequest"})
    @POST("/HuBeiCityAPIServer/index.php/huibeicityserver/loadmonthinfo")
    Call<ResponseBody> queryETCInfoList(@Body String str);
}
