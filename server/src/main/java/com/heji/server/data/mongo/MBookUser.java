package com.heji.server.data.mongo;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class MBookUser {
    @Ignore
    public static String[] AUTHORITYS = new String[]{"CREATOR", "USER"};
    String name;
    String authority;
}