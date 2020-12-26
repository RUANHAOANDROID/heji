package com.rh.heji.ui.export;


/**
 * Date: 2020/12/5
 * Author: 锅得铁
 * #
 */

public class QianJiExcel {
    //    时间	分类	类型	金额	账户1	账户2	备注	账单图片
    String time;
    String category;
    String type;
    String money;
    String account1;
    String account2;
    String remark;
    String urls;

    public QianJiExcel(String time, String category, String type, String money, String account1, String account2, String remark, String urls) {
        this.time = time;
        this.category = category;
        this.type = type;
        this.money = money;
        this.account1 = account1;
        this.account2 = account2;
        this.remark = remark;
        this.urls = urls;
    }

    public QianJiExcel() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getAccount1() {
        return account1;
    }

    public void setAccount1(String account1) {
        this.account1 = account1;
    }

    public String getAccount2() {
        return account2;
    }

    public void setAccount2(String account2) {
        this.account2 = account2;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "QianJiExcel{" +
                "time='" + time + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", money='" + money + '\'' +
                ", account1='" + account1 + '\'' +
                ", account2='" + account2 + '\'' +
                ", remark='" + remark + '\'' +
                ", urls='" + urls + '\'' +
                '}';
    }
}
