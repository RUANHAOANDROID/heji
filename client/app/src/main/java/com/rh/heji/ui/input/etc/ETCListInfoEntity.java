package com.rh.heji.ui.input.etc;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Date: 2020/10/28
 * Author: 锅得铁
 * #
 */
public class ETCListInfoEntity {
    @SerializedName("status")
    public String status;
    @SerializedName("data")
    public List<Info> data;

    public static class Info {
        @SerializedName("cardNo")
        public String cardNo;
        @SerializedName("payCardType")
        public String payCardType;
        @SerializedName("etcPrice")
        public int etcPrice;
        @SerializedName("exchargetime")
        public String exchargetime;
        @SerializedName("ex_enStationName")
        public String exEnStationName;
        @SerializedName("vehplate")
        public String vehplate;
        @SerializedName("province")
        public String province;
        @SerializedName("type")
        public int type;
    }
}
