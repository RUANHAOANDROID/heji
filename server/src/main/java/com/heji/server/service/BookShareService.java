package com.heji.server.service;

import com.heji.server.data.mongo.MBookShare;

public interface BookShareService {

    String generateCode(String bookId);

    MBookShare getShareBook(String code);
}
