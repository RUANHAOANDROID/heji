package com.heji.server.service;

import com.heji.server.data.mongo.MBillImage;

public interface ImageService {
    String saveImage(MBillImage image);

    MBillImage getImage(String imgId);

    void removeImages(String... imgId);

    void removeImage(String imgId);
}
