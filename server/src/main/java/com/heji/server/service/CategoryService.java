package com.heji.server.service;

import com.heji.server.data.mongo.MCategory;

import java.util.List;

public interface CategoryService {
    //保存分类标签
    String save(MCategory category);

    //查找单个分类
    MCategory find(String _id);

    List<MCategory> findByBookId(String book_id);

    String update(MCategory category);

    boolean delete(String _id);
}
