package com.rh.heji.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Category category);

    @Query("select _id from bill_category where _id=:id")
    String findCategoryID(String id);

    @Query("select * from bill_category where sync_status=:syncStatus")
    List<Category> findCategoryByStatic(int syncStatus);

    @Query("select * from bill_category where category=:name and type=:type")
    List<Category> findByNameAndType(String name, int type);

    @Query("select * from  bill_category where type =:type AND sync_status !=" + Constant.STATUS_DELETE)
    LiveData<List<Category>> findIncomeOrExpenditure(int type);

    @Query("select * from  bill_category where sync_status ==" + Constant.STATUS_DELETE + " or sync_status ==" + Constant.STATUS_NOT_SYNC)
    LiveData<List<Category>> observeNotUploadOrDelete();

    @Query("select * from bill_category where category =:category")
    List<Category> queryByCategoryName(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(Category category);

    @Delete
    void delete(Category category);
}
