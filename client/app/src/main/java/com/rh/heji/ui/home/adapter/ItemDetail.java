package com.rh.heji.ui.home.adapter;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
public class ItemDetail {
    public static final String TAG = "Detail";

    float money;
    String type;
    String record_time;//记账时间
    String ticket_time;//照片票据时间。以照片创建日期为准
    String user_label;//用户标签，费用产生人

    private ItemDetail() {

    }

    public static String getTAG() {
        return TAG;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }

    public String getTicket_time() {
        return ticket_time;
    }

    public void setTicket_time(String ticket_time) {
        this.ticket_time = ticket_time;
    }

    public String getUser_label() {
        return user_label;
    }

    public void setUser_label(String user_label) {
        this.user_label = user_label;
    }
}
