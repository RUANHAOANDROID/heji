package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.heji.server.data.mongo.MOperationLog.COLLATION_NAME;

/**
 * 操作记录。用户根据操作记录同步相关数据
 * 账本更改{加入用户，移除用户}
 * 账单更改{账单删除，账单更改}
 * 账单图片{图片删除，}
 */
@Data
@Accessors(chain = true)
@ToString
@Document(COLLATION_NAME)
public class MOperationLog {
    public static final String COLLATION_NAME = "operation_log";
    String _id;
    // delete ,update
    String type;
    //user
    String user;
    //book_id
    @Field("book_id")
    String bookId;

}
