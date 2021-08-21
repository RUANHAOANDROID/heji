package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

import static com.heji.server.data.mongo.MBook.COLLATION_NAME;

/**
 * 账本
 */
@Data
@Accessors(chain = true)
@ToString
@Document(COLLATION_NAME)
public class MBook {
    public static final String COLLATION_NAME = "book";
    @Id
    String _id;
    @Field("name")
    String name;
    /**
     * 创建人
     */
    @Field("creat_user")
    String createUser;
    /**
     * 账本类型
     */
    String type;
    /**
     * 账本用户
     */
    List<String> users;
    /**
     * 账本封面
     */
    byte[] banner;

}
