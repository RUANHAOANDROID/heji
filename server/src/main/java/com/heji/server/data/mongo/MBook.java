package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * 账本
 */
@Data
@Accessors(chain = true)
@ToString
@Document
public class MBook {
    @MongoId
    ObjectId book_id;
    @Field("book_name")
    String bookName;
}
