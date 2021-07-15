package com.rh.heji.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(category: Category  )

    @Query("select _id from category where _id=:id")
    fun findByID(id: String  ): String  

    @Query("select * from category where sync_status=:syncStatus")
    fun findCategoryByStatic(syncStatus: Int): List<Category  >  

    @Query("select * from category where category=:name and type=:type")
    fun findByNameAndType(name: String  , type: Int): MutableList<Category  >

    @Query("select * from  category where type =:type AND sync_status !=" + Constant.Companion.STATUS_DELETE + " ORDER BY `index` DESC,_id DESC ")
    fun findIncomeOrExpenditure(type: Int): LiveData<MutableList<Category>>

    @Query("select * from  category where sync_status ==" + Constant.Companion.STATUS_DELETE + " or sync_status ==" + Constant.Companion.STATUS_NOT_SYNC)
    fun observeNotUploadOrDelete(): LiveData<MutableList<Category  >  >

    @Query("select * from category where category =:category")
    fun queryByCategoryName(category: String  ): List<Category  >  

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(category: Category  )

    @Delete
    fun delete(category: Category  ) // updateOrders();
    @Query("delete from bill where id=:id")
    fun deleteById(id: String)
    //getSubListByParentId
    //getByType
    //    public final List<Category> assembleCategories(List<   extends Category> paramList) {
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