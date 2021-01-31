package com.heji.server.service;

import com.heji.server.data.mongo.MBillImage;

import java.util.List;

public interface ImageService {
    String saveImage(MBillImage image);

    MBillImage getImage(String imgId);

    List<MBillImage> getBillImages(String bill_id);

    boolean removeBillImages(String billId);

    void removeImages(String... imgId);

    void removeImage(String imgId);
}
