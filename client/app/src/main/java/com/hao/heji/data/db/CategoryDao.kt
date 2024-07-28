package com.hao.heji.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * @date: 2020/9/16
 * @author: 锅得铁
 * #
 */
@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(category: Category)

    @Query("select count(0) from category")
    fun count(): Int

    @Query("select count(0) from category where hashValue=:hasCode")
    fun exist(hasCode: Int): Int

    @Query("select _id from category where _id=:id")
    fun findByID(id: String): String

    @Query("select * from category where sync_status=:syncStatus")
    fun findCategoryByStatic(syncStatus: Int): List<Category>

    @Query("select * from category where name=:name and type=:type")
    fun findByNameAndType(name: String, type: Int): MutableList<Category>

    @Query("select * from  category where book_id=:bookID AND type =:type AND sync_status != ${STATUS.DELETED} ORDER BY `index` DESC,_id DESC ")
    fun observeIncomeOrExpenditure(bookID: String, type: Int): Flow<MutableList<Category>>

    @Query("select * from  category where book_id=:bookID AND type =:type AND sync_status != ${STATUS.DELETED} ORDER BY `index` DESC,_id DESC ")
    fun findIncomeOrExpenditure(bookID: String, type: Int): MutableList<Category>


    @Query("select * from  category where sync_status == ${STATUS.DELETED} or sync_status == ${STATUS.NEW}")
    fun observeNotUploadOrDelete(): Flow<MutableList<Category>>

    @Query("select * from category where name =:name")
    fun queryByCategoryName(name: String): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(category: Category)

    @Delete
    fun delete(category: Category) // updateOrders();

    @Query("delete from bill where bill_id=:id")
    fun deleteById(id: String)
}