package com.rh.heji.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rh.heji.data.db.Dealer;

import java.util.List;

/**
 * Date: 2020/9/22
 * Author: 锅得铁
 * #
 */
@Dao
public interface DealerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Dealer user);

    @Query("select * from bill_dealer")
    List<Dealer> findAll();
}
