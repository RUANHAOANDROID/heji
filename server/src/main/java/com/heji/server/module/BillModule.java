package com.heji.server.module;

import com.heji.server.data.mongo.MBill;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.BsonNumber;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Accessors(chain = true)
public class BillModule {
    private String bid;
    private Double money;
    private String category;
    private Integer type;
    private String dealer;
    private String remark;
    private String[] images;
    private long time;//选择的时间
    private long createTime;
    private long updateTime;

    public BillModule() {

    }

    public BillModule(MBill bill) {
        this.bid = bill.getBid();
        this.money = bill.getMoney();
        this.category = bill.getCategory();
        this.type = bill.getType();
        this.dealer = bill.getDealer();
        this.remark = bill.getRemark();
        this.images = bill.getImages();
        this.time = bill.getTime();
        this.createTime = bill.getCreateTime();
        this.updateTime = bill.getUpdateTime();
    }
}
