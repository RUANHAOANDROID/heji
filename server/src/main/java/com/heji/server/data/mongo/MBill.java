package com.heji.server.data.mongo;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.heji.server.data.mongo.MBill.COLLECTION_NAME;

//@Transient 普通字段注解
@Data//get set
@ToString//tostring
@Accessors(chain = true)//能让我们方便使用链式方法创建实体对象。
@Document(COLLECTION_NAME)
public class MBill {
    public static final String COLLECTION_NAME = "bill";
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
    @SerializedName("time")
    private String time;//选择的时间

    @CreatedDate
    @Field(name = "create_time")
    private long createTime;

    @Field(name = "update_time")
    private long updateTime;
    /**
     * 每次更新账单和账本时更新该锚点
     */
    Long modified;

    public MBill() {

    }
}
