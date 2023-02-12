package com.rh.heji;

import com.rh.heji.data.converters.DateConverters;
import com.rh.heji.data.converters.MoneyConverters;
import com.rh.heji.data.db.Bill;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class JsonJavaTest {
    @Test
    public void jsonTest() throws IOException {
        String jsonString = "{\"id\":\"611e854692891153fb00ae14\",\"bookId\":\"mybook\",\"money\":0.00,\"type\":-1,\"category\":null,\"time\":\"2021-22-20 00:22:30\",\"updateTime\":0,\"dealer\":null,\"createUser\":\"App.getInstance().currentUser.username\",\"remark\":null,\"imgCount\":0,\"syncStatus\":0,\"images\":[]}";
        Moshi moshi = new Moshi.Builder()
                .addLast(new KotlinJsonAdapterFactory())//Java 测试添加kotlin转换器
                .add(new DateAdapter())
                .add(new MoneyAdapter())
                .build();
        JsonAdapter<Bill> jsonAdapter = moshi.adapter(Bill.class);
        Bill billEntity = jsonAdapter.fromJson(jsonString);
        System.out.println(billEntity);
    }
}

class DateAdapter {
    @ToJson
    public String toJson(Date date) {
        return DateConverters.date2Str(date);
    }


    @FromJson
    public Date fromJson(String date) {
        return DateConverters.str2Date(date);
    }
}

class MoneyAdapter {

    @ToJson
    public String toJson(BigDecimal money) {
        return MoneyConverters.toString(money);
    }

    @FromJson
    public BigDecimal fromJson(String money) {
        return new BigDecimal(money);
    }
}

class Entity {
    String userName;
    int age;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}