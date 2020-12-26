package com.heji.server.data.bean;

import com.alibaba.excel.annotation.ExcelProperty;

public class QianjiExcelBean {
    //    时间	分类	类型	金额	账户1	账户2	备注	账单图片
    @ExcelProperty("时间")
    String time;
    @ExcelProperty("分类")
    String category;
    @ExcelProperty("类型")
    String type;
    @ExcelProperty("金额")
    String money;
    @ExcelProperty("账户1")
    String account1;
    @ExcelProperty("账户2")
    String account2;
    @ExcelProperty("备注")
    String remark;
    @ExcelProperty("账单图片")
    String urls;


    public QianjiExcelBean() {
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
