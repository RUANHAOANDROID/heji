package com.rh.heji.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.rh.heji.data.converters.MoneyConverters;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@Entity(tableName = "bill")
public class Bill {
    public static final String COLUMN_ID = "bill_id";
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "bill_id")
    public String id;
    /**
     * 钱
     */
    @TypeConverters(MoneyConverters.class)
    @ColumnInfo(name = "money")
    public BigDecimal money;
    /**
     * 收支类型 s|z
     */
    @NotNull
    @ColumnInfo(name = "type")
    public int type;
    /**
     * 类别
     */
    @ColumnInfo(name = "category")
    public String category;
    /**
     * 账单时间-产生费用的日期-以这个为主
     */
    @ColumnInfo(name = "bill_time")
    public long time;

    /**
     * 创建时间
     */
    @ColumnInfo(name = "create_time")
    public long createTime;//记账时间

    /**
     * 更新时间
     */
    @ColumnInfo(name = "update_time")
    public long updateTime;//记账时间

    /**
     * 用户标签，费用产生人
     */
    @ColumnInfo(name = "dealer")
    public String dealer;
    /**
     * 备注
     */
    @ColumnInfo(name = "remark")
    public String remark;

    @ColumnInfo(name = "img_count")
    private int imgCount;


    @ColumnInfo(name = "sync_status")
    int synced = Constant.STATUS_NOT_SYNC;

    public Bill() {

    }

    @Ignore
    public Bill(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(@NotNull int type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public long getBillTime() {
        return time;
    }

    public void setBillTime(long billTime) {
        this.time = billTime;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    @Ignore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return id.equals(bill.id);
    }

    @Ignore
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Ignore
    @Override
    public String toString() {
        return "Bill{" +
                "id='" + id + '\'' +
                ", money=" + money +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", time=" + time +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", dealer='" + dealer + '\'' +
                ", remark='" + remark + '\'' +
                ", imgCount=" + imgCount +
                ", synced=" + synced +
                '}';
    }
}
