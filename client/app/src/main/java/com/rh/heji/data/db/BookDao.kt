package com.rh.heji.data.db

import androidx.room.*

@Dao
interface BookDao {

    @Insert
    fun createNewBook(book: Book): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(book: Book): Int

    @Query("update book set id=:newId where id=:oldId")
    fun updateId(oldId: String, newId: String): Int

    @Query("select count() from book  where name=:name")
    fun countByName(name: String): Int

    @Query("select * from book")
    fun allBooks(): MutableList<Book>

    @Query("select * from book where sync_status=:status")
    fun books(status: Int = STATUS.NOT_SYNCED): MutableList<Book>

    @Transaction
    @Query("select * from book where name =:bookName")
    fun findBookWhitBills(bookName: String): MutableList<BookWithBills>

    @Query("select count(0) from book")
    fun count(): Int
}
