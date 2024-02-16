package com.rh.heji.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    fun insert(book: Book): Long

    @Insert(entity = Book::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsert(book: Book): Long

    @Update(entity = Book::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(book: Book): Int

    @Query("delete from book  where book_id=:bookId")
    fun deleteById(bookId: String)

    @Query("update book set sync_status = ${STATUS.DELETED} where book_id=:bookId")
    fun preDelete(bookId: String): Int

    @Query("select count() from book  where name=:name")
    fun countByName(name: String): Int

    @Query("SELECT * FROM book WHERE sync_status!=${STATUS.DELETED} ORDER BY book_id")
    fun allBooks(): Flow<MutableList<Book>>

    @Query("select * from book where sync_status=:status")
    fun books(status: Int): MutableList<Book>

    @Query("select is_initial from book where book_id=:bookId")
    fun isInitialBook(bookId: String): Int


    @Query("select count(0) from book")
    fun count(): Int

    @Query("select count(0) from book WHERE book_id=:bookId")
    fun exist(bookId: String): Int

    @Query("select * from book where book_id =:id")
    fun findBook(id: String): MutableList<Book>

    @Query("SELECT book_id FROM book WHERE crt_user_id=:crtUserId")
    fun findBookIdsByUser(crtUserId: String): MutableList<String>
}
