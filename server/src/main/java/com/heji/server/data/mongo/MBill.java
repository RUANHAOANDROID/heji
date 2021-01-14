package com.heji.server.data.mongo;

import com.heji.server.data.converts.PathConverter;
import com.heji.server.module.BillModule;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.BsonDateTime;
import org.bson.BsonNumber;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.util.List;

import static com.heji.server.data.mongo.MBill.COLLATION_NAME;

//@Transient 普通字段注解
@Data//get set
@ToString//tostring
@Accessors(chain = true)//能让我们方便使用链式方法创建实体对象。
@Document(COLLATION_NAME)
public class MBill {
    public static final String COLLATION_NAME = "bill";
    @Id
    private String _id;
    //账本ID
    private String bookId;

    private Double money;

    private String category;

    private Integer type;

    private String dealer;

    private String remark;

    private String[] images;

    private long time;//选择的时间

    @CreatedDate
    @Field(name = "create_time")
    private long createTime;

    @Field(name = "update_time")
    private long updateTime;

    public MBill() {

    }

    public MBill(BillModule billModule) {
        _id = billModule.get_id();
        bookId = billModule.getBookId();
        money = billModule.getMoney();
        category = billModule.getCategory();
        type = billModule.getType();
        dealer = billModule.getDealer();
        remark = billModule.getRemark();
        time = billModule.getTime() ;
        createTime = billModule.getCreateTime();
        updateTime = billModule.getUpdateTime();
    }
}
