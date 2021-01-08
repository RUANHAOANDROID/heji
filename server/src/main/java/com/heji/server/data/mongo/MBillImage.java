package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

/**
 * 账单票据文件
 */
@Data
@Accessors(chain = true)
@ToString
@Document
public class MBillImage implements Serializable {
    private static final long serialVersionUID = 1L;
    @MongoId
    private ObjectId imgId;
    // 所属账单ID
    private String billId;
    // 上传文件名(*.*)
    private String filename;
    // 原文件大小
    private Long length;
    // 原文件MD5
    private String md5;
    // 上传时间
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
