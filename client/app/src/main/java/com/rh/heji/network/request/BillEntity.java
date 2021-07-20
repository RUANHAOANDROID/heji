package com.rh.heji.network.request;

import com.rh.heji.data.converters.DateConverters;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.Image;
import com.rh.heji.data.db.STATUS;

import java.math.BigDecimal;
import java.util.List;


/**
 * Date: 2020/9/23
 * Author: 锅得铁
 * # 服务端接受的JSON类型
 */
public class BillEntity {

    private String _id;
    private String bookId;
    private String money;

    private String category;

    private int type;

    private String dealer;
    private String createUser;
    private String remark;

    private List<String> images;

    private String time;

    private long createTime;

    private long updateTime;

    public BillEntity(Bill bill) {
        this._id = bill.getId();
        this.money = bill.getMoney().toString();
        this.category = bill.getCategory();
        this.type = bill.getType();
        this.dealer = bill.getDealer();
        this.remark = bill.getRemark();
        this.time = DateConverters.date2Str(bill.getBillTime());
        this.createTime = bill.getCreateTime();
        this.updateTime = bill.getUpdateTime();
        this.createUser = bill.getCreateUser();
        int imgCount = bill.getImgCount();
    }

    public Bill toBill() {
        Bill bill = new Bill();
        bill.setId(_id);
        bill.setMoney(new BigDecimal(money));
        bill.setType(type);
        bill.setRemark(remark);
        bill.setDealer(dealer);
        bill.setCategory(category);
        bill.setCreateTime(createTime);
        bill.setBillTime(DateConverters.str2Date(time));
        bill.setSynced(STATUS.SYNCED);
        bill.setUpdateTime(updateTime);
        bill.setCreateUser(createUser);
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                Image image = new Image("","");
                image.setOnlinePath(images.get(i));
            }
        }
        return bill;
    }

    public String getId() {
        return _id;
    }

    public void setId(String uid) {
        this._id = uid;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public String getBillTime() {
        return time;
    }

    public void setBillTime(String billTime) {
        this.time = billTime;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}
