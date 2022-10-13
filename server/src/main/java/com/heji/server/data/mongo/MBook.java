package com.heji.server.data.mongo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
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
     * 账本类型
     */
    String type;
    /**
     * 账本用户
     */
    List<MBookUser> users;
    /**
     * 账本封面
     */
    byte[] banner;

    Integer only;
    /**
     * 是否是初始账本，初始账本无法删除，每位用户仅有一个初始账本
     * 0 true 1 false
     */
    @SerializedName("first_book")
    Integer firstBook;
    /**
     * 每次更新账单和账本时更新该锚点
     */
    Long modified;
}
