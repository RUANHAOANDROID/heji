package com.rh.heji.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

/**
 * Date: 2021/3/2
 * Author: 锅得铁
 * #
 */
@Dao
public interface ErrorLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void install(ErrorLog errorLog);

}
