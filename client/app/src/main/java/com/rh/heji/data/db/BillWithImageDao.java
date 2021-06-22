package com.rh.heji.data.db;

import androidx.room.Insert;

/**
 * Date: 2021/6/22
 * Author: 锅得铁
 * #
 */
public interface BillWithImageDao {
    @Insert(entity = BillWithImage.class)
    void inserts(BillWithImage billWithImage);
}
