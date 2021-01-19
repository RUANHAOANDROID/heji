package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.heji.server.data.mongo.MUser.COLLATION_NAME;


@Data
@Accessors(chain = true)
@ToString
@Document(COLLATION_NAME)
public class MUser {
    public static final String COLLATION_NAME = "user";
    @Id
    String _id;
    String name;
    String password;
    String role;//角色权限
    String code;//邀请码
}
