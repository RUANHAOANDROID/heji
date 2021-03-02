package com.rh.heji.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
    String findByID(String id);

    @Query("select * from bill_category where sync_status=:syncStatus")
    List<Category> findCategoryByStatic(int syncStatus);

    @Query("select * from bill_category where category=:name and type=:type")
    List<Category> findByNameAndType(String name, int type);

    @Query("select * from  bill_category where type =:type AND sync_status !=" + Constant.STATUS_DELETE + " ORDER BY `index` DESC,_id DESC ")
    LiveData<List<Category>> findIncomeOrExpenditure(int type);

    @Query("select * from  bill_category where sync_status ==" + Constant.STATUS_DELETE + " or sync_status ==" + Constant.STATUS_NOT_SYNC)
    LiveData<List<Category>> observeNotUploadOrDelete();

    @Query("select * from bill_category where category =:category")
    List<Category> queryByCategoryName(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(Category category);

    @Delete
    void delete(Category category);

    @Update()
    void updateOrders();
    //getSubListByParentId
    //getByType
//    public final List<Category> assembleCategories(List<? extends Category> paramList) {
//        LinkedHashMap linkedHashMap = new LinkedHashMap();
//        for (Category category : paramList) {
//            if (category.isParentCategory()) {
//                linkedHashMap.put(Long.valueOf(category.getId()), category);
//                continue;
//            }
//            if (category.isSubCategory()) {
//                Category category1 = (Category)linkedHashMap.get(Long.valueOf(category.getParentId()));
//                if (category1 != null)
//                    category1.addSubCategory(category, false);
//            }
//        }
//        Collection collection = linkedHashMap.values();
//        return f.a(collection);
//    }
}
