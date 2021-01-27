package com.rh.heji.network.request;

import com.rh.heji.data.db.Category;

/**
 * Date: 2020/9/24
 * Author: 锅得铁
 * #标签
 */
public class CategoryEntity {

    private String name;

    private Integer type;

    private Integer level;

    public CategoryEntity(Category category) {
        this.name = category.getCategory();
        this.type = category.getType();
        this.level = category.getLevel();
    }

    public Category toDbCategory() {
        Category category = new Category(name, level, type);
        return category;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
