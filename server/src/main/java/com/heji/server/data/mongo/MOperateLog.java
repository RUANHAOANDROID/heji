package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import static com.heji.server.data.mongo.MOperateLog.COLLATION_NAME;

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
public class MOperateLog {
    public static final String COLLATION_NAME = "operate_log";
    public static final int DELETE = 0;
    public static final int UPDATE = 1;

    public static final int BOOK = 0;
    public static final int BILL = 1;
    public static final int CATEGORY = 2;

    @Id
    String _id;

    String targetId;//操作对象的ID，根据ID同步本地数据库

    Integer type; // delete ,update

    Integer optClass;//操作的可以是账本，账单，类别

    Date date;
}