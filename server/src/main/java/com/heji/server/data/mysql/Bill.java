package com.heji.server.data.mysql;

import com.heji.server.data.converts.PathConverter;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "hj_bill")
public class Bill {
    @Id
    @Column(name = "uid")
    private String uid;

    @Column(name = "money")
    private String money;

    @Column(name = "category")
    private String category;

    @Column(name = "type")
    private Integer type;

    @Column(name = "dealer")
    private String dealer;

    @Column(name = "remark")
    private String remark;

    @Convert(converter = PathConverter.class)
    @Column(name = "images")
    private List<String> images;

    @Column(name = "time")
    private long time;//选择的时间

    @Column(name = "create_time")
    private long createTime;

    @Column(name = "update_time")
    private long updateTime;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "uid='" + uid + '\'' +
                ", money='" + money + '\'' +
                ", category='" + category + '\'' +
                ", type=" + type +
                ", dealer='" + dealer + '\'' +
                ", remark='" + remark + '\'' +
                ", images=" + images +
                ", time=" + time +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
