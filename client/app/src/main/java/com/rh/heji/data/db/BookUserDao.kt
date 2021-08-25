package com.rh.heji.data.db

import androidx.room.*

@Dao
interface BookUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: BookUser)

    @Update(entity = BookUser::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg users: BookUser)

    //~~~~~~~~~~~~~~~~~~~~~~~~book whit users~~~~~~~~~~~~~~~~~~~~~~~~~~//
    @Transaction
    @Query("select * from book")
    fun booksWhitUsers(): MutableList<BookWhitUsers>

    @Delete
    fun delete(vararg user: BookUser)
}
