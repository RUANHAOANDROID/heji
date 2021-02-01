package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

//import static com.heji.server.data.mongo.Authority.COLLECTION_NAME;

@Data
@ToString
@Accessors(chain = true)
public class Authority implements GrantedAuthority {
    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";
    //public static final String COLLECTION_NAME = "authority";
    String authority;
    String book_id;

    @Override
    public String getAuthority() {
        return authority;
    }
}
