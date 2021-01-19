package com.heji.server.data.mongo;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.heji.server.data.mongo.MCategory.COLLECTION_NAME;

@Data
@ToString
@Accessors(chain = true)
@Document("user")
public class MUser {
    public static String COLLATION_NAME = "user";
    @Id
    String _id;
    String name;
    String password;
    String tel;
    String role;//角色权限
    @Ignore
    String code;//邀请码

}
