package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import static com.heji.server.data.mongo.MCategory.COLLECTION_NAME;

@Data
@ToString
@Accessors(chain = true)
@Document(COLLECTION_NAME)
public class MCategory {
    public static final String COLLECTION_NAME = "category";
    @Id
    String _id;

    //book_id对应 账本ID
    @Field("book_id")
    String bookId;

    Integer type;
    String name;
    Integer level;
    Integer index;//常用位置排序
}
