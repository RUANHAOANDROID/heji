package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.Date;

import static com.heji.server.data.mongo.MBookShare.COLLATION_NAME;

@Data
@Accessors(chain = true)
@ToString
@Document(COLLATION_NAME)
public class MBookShare {
    public static final String COLLATION_NAME = "book_share";
    public static final int DELAY_SECOND = 60 * 60 * 24 * 7;//3 days

    @Id
    String _id;

    String code;

    @Field("book_id")
    String bookId;

    @Indexed(name = "expiredTime", expireAfterSeconds = DELAY_SECOND)
    Date expiredTime;
}
