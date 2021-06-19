package com.rh.heji.data.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * Date: 2020/9/15
 * Author: 锅得铁
 * #
 */
public class BillWithImage {
    @Embedded
    public Bill bill;

    @Relation(
            parentColumn = "_bid",
            entityColumn = "_id"
    )
    public List<Image> images;
}
