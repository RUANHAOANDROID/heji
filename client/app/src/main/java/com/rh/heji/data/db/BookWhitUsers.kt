package com.rh.heji.data.db

import androidx.room.Embedded
import androidx.room.Relation

data class BookWhitUsers(
    @Embedded
    val book: Book? = null,
    @Relation(parentColumn =Book.COLUMN_ID, entityColumn = BookUser.COLUMN_ID)
    val users: MutableList<BookUser>? = null
){

}