package com.heji.server.service;

import com.heji.server.data.mongo.MErrorLog;

import java.util.List;

public interface ErrorLogService {
    void uploadErrorLog(MErrorLog errorLog);

    List<MErrorLog> getErrorLog();

    void deleteErrorLog(String errorLogId);

}
