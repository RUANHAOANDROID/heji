package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.heji.server.data.mongo.MBillBackup.COLLECTION_NAME;

//@Transient 不存入库临时字段
@Data//get set
@ToString//tostring
@Accessors(chain = true)//能让我们方便使用链式方法创建实体对象。
@Document(COLLECTION_NAME)
public class MBillBackup {
    public static final String COLLECTION_NAME = "bill_backup";
    @Id
    private String _id;
    //账本ID
    @Field(name = "book_id")
    private String bookId;

    private Double money;

    private String category;

    private Integer type;

    private String dealer;

    private String createUser;

    private String remark;

    private String[] images;

    private String time;//选择的时间

    @CreatedDate
    @Field(name = "create_time")
    private long createTime;

    @Field(name = "update_time")
    private long updateTime;

    public MBillBackup() {

    }

    public MBillBackup(MBill mBill) {
        _id = mBill.get_id();
        bookId = mBill.getBookId();
        money = mBill.getMoney();
        category = mBill.getCategory();
        type = mBill.getType();
        dealer = mBill.getDealer();
        remark = mBill.getRemark();
        time = mBill.getTime();
        images = mBill.getImages();
        createTime = mBill.getCreateTime();
        updateTime = mBill.getUpdateTime();
        createUser = mBill.getCreateUser();
    }
}
