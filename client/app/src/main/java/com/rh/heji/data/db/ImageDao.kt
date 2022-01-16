package com.rh.heji.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Date: 2020/11/19
 * Author: 锅得铁
 * #
 */
@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(ticket: Image )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(ticket: MutableList<Image >)

    @Delete
    fun delete(ticket: Image )

    @Query("delete from image where bill_id =:billID")
    fun deleteBillImage(billID: String )

    @Query("select image_id from image where bill_id =:billID")
    fun findImagesId(billID: String ):MutableList<String>

    @Transaction
    @Query("update image set local_path=:imagePath, sync_status =:status where image_id =:id")
    fun updateImageLocalPath(id: String , imagePath: String , status: Int): Int

    @Transaction
    @Query("update image set online_path=:onlinePath, sync_status=:status  where image_id =:imgId")
    fun updateOnlinePath(imgId: String , onlinePath: String , status: Int): Int

    @Query("select * from image where online_path=:path")
    fun findByOnLinePath(path: String ): MutableList<Image >

    @Query("DELETE FROM image WHERE " + Image.COLUMN_ID + "=:imgID")
    fun deleteById(imgID: String )

    @Query("SELECT * FROM image WHERE bill_id =:billId")
    fun findByBillId(billId: String ): Flow<MutableList<Image>>

    @Query("SELECT * FROM image WHERE image_id =:id")
    fun findById(id: String ): MutableList<Image >

    @Query("SELECT * FROM image WHERE bill_id =:billId AND sync_status== ${STATUS.NOT_SYNCED}")
    fun findByBillIdNotAsync(billId: String): MutableList<Image>

    @Query("SELECT * FROM image WHERE (local_path ISNULL OR local_path=='') AND(online_path!='' OR online_path != NULL)")
    fun observerNotDownloadImages(): Flow<MutableList<Image>>
}