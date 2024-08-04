package com.hao.heji.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    fun insert(book: Book): Long

    @Update(entity = Book::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(book: Book): Int

    @Query("DELETE FROM book  WHERE book_id=:bookId")
    fun deleteById(bookId: String)

    @Query("UPDATE book SET sync_status =:status WHERE book_id=:bookId")
    fun preDelete(bookId: String,status:Int=STATUS.DELETED): Int

    @Query("select count() from book  where name=:name")
    fun countByName(name: String): Int

    @Query("SELECT * FROM book WHERE sync_status!=:status ORDER BY book_id")
    fun allBooks(status: Int=STATUS.DELETED): Flow<MutableList<Book>>

    @Query("SELECT * FROM book WHERE crt_user_id=:userID AND is_initial == 1")
    fun findInitBook(userID: String): MutableList<Book>

    @Query("SELECT is_initial FROM book WHERE book_id=:bookId")
    fun isInitialBook(bookId: String): Int

    @Query("SELECT * FROM book WHERE sync_status!=:status")
    fun flowNotSynced(status: Int=STATUS.SYNCED):Flow<MutableList<Book>>

    @Query("SELECT count(0) FROM book")
    fun count(): Int

    @Query("SELECT count(0) FROM book WHERE book_id=:bookId")
    fun exist(bookId: String): Int

    @Query("SELECT * FROM book WHERE book_id =:id")
    fun findBook(id: String): MutableList<Book>

    @Query("SELECT book_id FROM book WHERE crt_user_id=:crtUserId")
    fun findBookIdsByUser(crtUserId: String): MutableList<String>
}
