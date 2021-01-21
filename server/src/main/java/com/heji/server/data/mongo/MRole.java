package com.heji.server.data.mongo;

import com.heji.server.data.mongo.constant.RoleName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static com.heji.server.data.mongo.MRole.COLLECTION_NAME;

@Data
@ToString
@Accessors(chain = true)
@Document(COLLECTION_NAME)
public class MRole {
    public static final String COLLECTION_NAME = "role";
    @Enumerated(EnumType.STRING)
    RoleName roleName;
}
