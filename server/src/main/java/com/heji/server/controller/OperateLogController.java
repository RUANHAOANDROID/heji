package com.heji.server.controller;

import com.heji.server.data.mongo.MOperateLog;
import com.heji.server.result.Result;
import com.heji.server.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/operateLog")
@Slf4j
public class OperateLogController {
    final OperateLogService operateLogService;

    public OperateLogController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    /**
     * @param bookId 账本ID
     * @return 操作日志
     */
    @ResponseBody
    @GetMapping(value = {"/delete"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getDeleteLog(@RequestParam String bookId) {
        List<MOperateLog> deleteLog = operateLogService.getOperateLogs(bookId);
        return Result.success(deleteLog);
    }
    /**
     * @param bookId 账本ID
     * @return 操作日志
     */
    @ResponseBody
    @GetMapping(value = {"/update"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getUpdateLog(@RequestParam String bookId) {
        List<MOperateLog> deleteLog = operateLogService.getUpdateLog(bookId,"2021-01-01","2021-09-07");
        return Result.success(deleteLog);
    }
}
