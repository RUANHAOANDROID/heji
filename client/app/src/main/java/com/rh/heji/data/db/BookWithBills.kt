package com.rh.heji.data.db

import androidx.room.Embedded
import androidx.room.Relation

/**
 *Date: 2021/7/15
 *Author: 锅得铁
 *#
 */
data class BookWithBills(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "id",
        entityColumn = "book_id"
    ) val bills: MutableList<Bill>
)