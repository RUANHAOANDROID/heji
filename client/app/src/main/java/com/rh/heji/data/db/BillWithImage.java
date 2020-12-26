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
            parentColumn = "bill_id",
            entityColumn = "bill_img_id"
    )
    public List<Image> images;

}
