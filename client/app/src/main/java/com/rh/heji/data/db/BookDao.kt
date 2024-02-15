package com.rh.heji.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull

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

    @Query("update book set sync_status = ${STATUS.DELETED} where book_id=:book_id")
    fun preDelete(book_id: String): Int

    @Query("select count() from book  where name=:name")
    fun countByName(name: String): Int

    @Query("SELECT * FROM book WHERE sync_status!=${STATUS.DELETED} ORDER BY book_id")
    fun allBooks(): Flow<MutableList<Book>>

    @Query("select * from book where sync_status=:status")
    fun books(status: Int): MutableList<Book>

    @Query("select isInitial from book where book_id=:book_id")
    fun isFirstBook(book_id: String): Int


    @Query("select count(0) from book")
    fun count(): Int

    @Query("select count(0) from book WHERE book_id=:bookId")
    fun exist(bookId: String): Int

    @Query("select * from book where book_id =:id")
    fun findBook(id: String): MutableList<Book>

    @Query("SELECT book_id FROM book WHERE create_user=:user")
    fun findBookIdsByUser(user: String): MutableList<String>
}
