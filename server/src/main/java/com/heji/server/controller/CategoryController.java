package com.heji.server.controller;

import com.heji.server.data.mongo.MCategory;
import com.heji.server.data.mongo.MOperateLog;
import com.heji.server.exception.NotFoundException;
import com.heji.server.exception.OperationException;
import com.heji.server.result.Result;
import com.heji.server.service.CategoryService;
import com.heji.server.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
//@Controller // This means that this class is a Controller
@RestController//json controller
@RequestMapping(path = "/category") // This means URL's start with /demo (after Application path)
public class CategoryController {

    final CategoryService categoryService;
    final OperateLogService operateLogService;
    public CategoryController(CategoryService categoryService, OperateLogService operateLogService) {
        this.categoryService = categoryService;
        this.operateLogService = operateLogService;
    }


    @ResponseBody
    @PostMapping(value = {"/add"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String add(@RequestBody MCategory category) {
        String _id = categoryService.save(category);
        return Result.success(_id);
    }

    @ResponseBody
    @PostMapping(value = {"/addCategories"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addCategories(@RequestBody List<MCategory> category) {
        List<String> _ids = categoryService.saveAll(category);
        return Result.success(_ids);
    }

    @ResponseBody
    @GetMapping(value = {"/getByBookId"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getCategories(@RequestParam(defaultValue = "0") String book_id) {
        List<MCategory> mCategories = categoryService.findByBookId(book_id);
        if (Objects.isNull(mCategories) || mCategories.size() <= 0)
            return Result.error("类别不存在");
        return Result.success(mCategories);
    }

    @ResponseBody
    @GetMapping(value = {"/getAllCategory"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllCategory(@RequestParam(defaultValue = "0") String book_id) {
        List<MCategory> mCategories = categoryService.findByBookId(book_id);
        if (Objects.isNull(mCategories) || mCategories.size() <= 0)
            throw new NotFoundException("类别不存在");
        return Result.success(mCategories);
    }

    @ResponseBody
    @GetMapping(value = {"/delete"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteCategory(@RequestParam() String _id) {
        boolean isOk = categoryService.delete(_id);
        if (!isOk) {
            throw new OperationException("删除失败");
        }
        //写入操作日志
        operateLogService.addOperateLog(new MOperateLog()
                .setTargetId(_id)
                .setType(MOperateLog.DELETE)
                .setDate(new Date())
                .setOptClass(MOperateLog.CATEGORY));
        return Result.success("删除成功");
    }
}
