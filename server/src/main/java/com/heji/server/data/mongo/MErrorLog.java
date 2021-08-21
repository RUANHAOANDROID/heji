package com.heji.server.data.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.heji.server.data.mongo.MErrorLog.COLLECTION_NAME;


@Data
@ToString
@Accessors(chain = true)
@Document(COLLECTION_NAME)
public class MErrorLog {
    public static final String COLLECTION_NAME = "error_log";
    @Id
    String _id;
    String uid;
    String tel;
    String timeOfCrash;
    String romInfo;
    String deviceManufacturer;
    String deviceModel;
    String sdkVersionCode;
    String sdkVersionName;
    String appVersionName;
    String appVersionCode;
    Boolean isEmulator;
    Boolean isTablet;
    String uniqueDeviceId;
    String networkType;
    String throwable;
}
