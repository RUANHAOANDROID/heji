package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

import static com.heji.server.data.mongo.MBillImage.COLLECTION_NAME;

/**
 * 账单票据文件
 */
@Data
@Accessors(chain = true)
@ToString
@Document(COLLECTION_NAME)
public class MBillImage implements Serializable {
    public static final String COLLECTION_NAME = "bill_image";
    private static final long serialVersionUID = 1L;
    @Id
    private String _id;
    // 所属账单ID
    @Field("bill_id")
    private String billId;
    // 上传文件名(*.*)
    private String filename;
    // 原文件大小
    private Long length;
    // 原文件MD5
    private String md5;
    // 上传时间
    @Field("upload_time")
    private Long uploadTime;
    // 文件后缀名
    private String ext;
    // 是否GridFS保存
    private Boolean isGridFS;
    // isGridFS=true  gridFs文件ID false:文档文件ID
    private Object fileId;
    //二进制文件
    private byte[] data;
}
