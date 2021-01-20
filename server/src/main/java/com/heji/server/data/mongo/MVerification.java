package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.heji.server.data.mongo.MVerification.COLLECTION_NAME;

@Data
@ToString
@Accessors(chain = true)
@Document(COLLECTION_NAME)
public class MVerification {
    public static final String COLLECTION_NAME = "verification";
    @Id
    String _id;
    String code;
}
