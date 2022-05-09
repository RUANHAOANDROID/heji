package com.rh.heji.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Date: 2020/9/22
 * @author: 锅得铁
 * #
 */
@Dao
interface DealerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: Dealer?)

    @Query("select * from dealer")
    fun findAll(): List<Dealer>
}