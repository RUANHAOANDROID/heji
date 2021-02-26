package com.heji.server.module;

import com.heji.server.data.mongo.MBill;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.BsonDateTime;
import org.bson.BsonNumber;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Accessors(chain = true)
public class BillModule {
    private String _id;
    private String bookId;
    private Double money;
    private String category;
    private Integer type;
    private String dealer;
    private String createUser;
    private String remark;
    private String[] images;
    private String time;//选择的时间
    private long createTime;
    private long updateTime;

    public BillModule() {

    }

    public BillModule(MBill bill) {
        this._id = bill.get_id();
        this.bookId = bill.getBookId();
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
