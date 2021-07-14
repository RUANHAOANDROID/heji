package com.rh.heji.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {

    @Insert
    fun createNewBook(book: Book): Long

    @Query("select count() from book  where name=:name")
    fun countByName(name: String): Int

    @Query("select * from book")
    fun books(): MutableList<Book>
}
