package com.rh.heji.data.db

import androidx.room.*
import com.rh.heji.data.AppDatabase
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

    @Transaction
    fun installBillAndImages(bill:Bill,image:MutableList<Image>):Long{
        var count =AppDatabase.getInstance().billDao().install(bill)
        install(image)
        return count
    }

    @Delete
    fun delete(ticket: Image )



    @Query("DELETE FROM image WHERE bill_id =:billID")
    fun deleteBillImage(billID: String )

    @Query("SELECT image_id FROM image WHERE bill_id =:billID")
    fun findImagesId(billID: String ):MutableList<String>

    @Transaction
    @Query("UPDATE image SET sync_status = ${STATUS.DELETED} WHERE image_id =:imageID")
    fun preDelete(imageID: String )

    @Transaction
    @Query("UPDATE image SET local_path=:imagePath, sync_status =:status WHERE image_id =:id")
    fun updateImageLocalPath(id: String , imagePath: String , status: Int): Int

    @Transaction
    @Query("UPDATE image SET online_path=:onlinePath, sync_status=:status  WHERE image_id =:imgId")
    fun updateOnlinePath(imgId: String , onlinePath: String , status: Int): Int

    @Query("SELECT * FROM image WHERE online_path=:path")
    fun findByOnLinePath(path: String ): MutableList<Image >

    @Query("DELETE FROM ${Image.TAB_NAME} WHERE ${Image.COLUMN_ID}=:imgID")
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