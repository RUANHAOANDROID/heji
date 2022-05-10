package com.rh.heji.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

/**
 * Date: 2021/3/2
 * @author: 锅得铁
 * #
 */
@Dao
interface ErrorLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(errorLog: ErrorLog?)
}