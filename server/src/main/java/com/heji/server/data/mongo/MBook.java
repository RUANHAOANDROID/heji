package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
    @Field("book_name")
    String bookName;
}
