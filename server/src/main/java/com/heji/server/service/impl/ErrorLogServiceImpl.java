package com.heji.server.service.impl;

import com.heji.server.data.mongo.MErrorLog;
import com.heji.server.data.mongo.repository.MErrorLogRepository;
import com.heji.server.service.ErrorLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ErrorLogService")
public class ErrorLogServiceImpl implements ErrorLogService {
    final MErrorLogRepository mErrorLogRepository;

    public ErrorLogServiceImpl(MErrorLogRepository mErrorLogRepository) {
        this.mErrorLogRepository = mErrorLogRepository;
    }

    @Override
    public void uploadErrorLog(MErrorLog errorLog) {
        mErrorLogRepository.save(errorLog);
    }

    @Override
    public List<MErrorLog> getErrorLog() {
        return mErrorLogRepository.findAll();
    }

    @Override
    public void deleteErrorLog(String errorLogId) {
        mErrorLogRepository.deleteById(errorLogId);
    }


}
