package com.heji.server.data.controller;

import com.heji.server.data.Category;
import com.heji.server.data.repository.CategoryDao;
import com.heji.server.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
//@Controller // This means that this class is a Controller
@RestController//json controller
@RequestMapping(path = "/category") // This means URL's start with /demo (after Application path)
public class CategoryController {
    final CategoryDao categoryDao;

    public CategoryController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @ResponseBody
    @PostMapping(value = {"/addCategories"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String add(@RequestBody Category category) {
        boolean exists = categoryDao.existsDistinctByName(category.getName());
        if (exists) {
            //categoryDao.updateCategory(category.getType(), category.getLevel(), category.getName());
            categoryDao.delete(category);
        }
        categoryDao.save(category);

        return Result.success(category.getName());
    }

    @ResponseBody
    @GetMapping(value = {"/getCategories"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getCategorys(@RequestParam(defaultValue = "0") int type, @RequestParam(defaultValue = "0") int level) {
        List<Category> categories;
        if (type == 0 && level == 0) {
            categories = categoryDao.findAll();
        } else {
            categories = categoryDao.findAllByTypeAndLevel(type, level);
        }

        return Result.success(categories);
    }

    @ResponseBody
    @GetMapping(value = {"/deleteCategory"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteCategory(@RequestParam() String categoryName) {
        categoryDao.deleteCategoryByName(categoryName);
        return Result.success(categoryName);
    }

}
