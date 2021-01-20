package com.heji.server.module;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Accessors(chain = true)
public class UserInfo {
    @Id
    String _id;
    String name;
    String password;
    String tel;
    String role;//角色权限
    @Ignore
    String code;//邀请码
}
