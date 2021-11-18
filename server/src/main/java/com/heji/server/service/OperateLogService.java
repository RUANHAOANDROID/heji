package com.heji.server.service;

import com.heji.server.data.mongo.MOperateLog;

import java.util.List;

public interface OperateLogService {
    void addOperateLog(MOperateLog object);
    List<MOperateLog> getOperateLogs(String book_id);
}
