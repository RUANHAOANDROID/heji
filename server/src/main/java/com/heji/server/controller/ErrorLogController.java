package com.heji.server.controller;

import com.heji.server.data.mongo.MErrorLog;
import com.heji.server.model.base.ApiResponse;
import com.heji.server.service.ErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/log")
@Slf4j
public class ErrorLogController {
    final ErrorLogService errorLogService;

    public ErrorLogController(ErrorLogService logService) {
        this.errorLogService = logService;
    }

    @ResponseBody
    @GetMapping(value = {"/get"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getErrorLog() {
        List<MErrorLog> logs = errorLogService.getErrorLog();
        return ApiResponse.success(logs);
    }

    @ResponseBody
    @PostMapping(value = {"/upload"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void uploadErrorLog(@RequestBody MErrorLog errorLog) {
        errorLogService.uploadErrorLog(errorLog);
    }
}
