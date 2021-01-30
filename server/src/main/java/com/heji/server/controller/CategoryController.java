package com.heji.server.controller;

import com.heji.server.data.mongo.MCategory;
import com.heji.server.exception.DeleteException;
import com.heji.server.exception.NotFindException;
import com.heji.server.result.Result;
import com.heji.server.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
//@Controller // This means that this class is a Controller
@RestController//json controller
@RequestMapping(path = "/category") // This means URL's start with /demo (after Application path)
public class CategoryController {

    final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
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
            throw new NotFindException("类别不存在");
        return Result.success(mCategories);
    }

    @ResponseBody
    @GetMapping(value = {"/getAllCategory"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllCategory(@RequestParam(defaultValue = "0") String book_id) {
        List<MCategory> mCategories = categoryService.findByBookId(book_id);
        if (Objects.isNull(mCategories) || mCategories.size() <= 0)
            throw new NotFindException("类别不存在");
        return Result.success(mCategories);
    }

    @ResponseBody
    @GetMapping(value = {"/delete"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteCategory(@RequestParam() String _id) {
        boolean isOk = categoryService.delete(_id);
        if (!isOk) {
            throw new DeleteException("删除失败");
        }
        return Result.success("删除成功");
    }
}
