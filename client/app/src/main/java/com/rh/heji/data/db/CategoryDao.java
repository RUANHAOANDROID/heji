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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Query("select * from  bill_category where type =:type AND sync_status !=" + Category.STATUS_DELETE)
    LiveData<List<Category>> findIncomeOrExpenditure(int type);

    @Query("select * from  bill_category where sync_status ==" + Category.STATUS_DELETE + " or sync_status ==" + Category.STATUS_NOT_SYNC)
    LiveData<List<Category>> observeNotUploadOrDelete();

    @Query("select category from bill_category where category =:name")
    String existsByName(String name);

    @Query("select * from bill_category where category =:category")
    List<Category> queryByCategoryName(String category);

    @Query("select * from bill_category where level =:level")
    List<Category> queryByLevel(int level);


    @Query("select * from bill_category where type =:type")
    List<Category> queryByType(int type);

    @Query("select * from bill_category where sync_status =:syncStatus ")
    List<Category> queryBillByStatus(int syncStatus);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(Category category);

    @Delete
    void delete(Category category);
}
