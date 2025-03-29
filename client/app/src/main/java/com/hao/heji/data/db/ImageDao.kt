package com.hao.heji.data.db

import androidx.room.*
import com.hao.heji.App
import kotlinx.coroutines.flow.Flow

/**
 * @date: 2020/11/19
 * @author: 锅得铁
 * #
 */
@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(ticket: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(ticket: MutableList<Image>)

    @Transaction
    fun installBillAndImages(bill: Bill, image: MutableList<Image>): Long {
        var count = App.dataBase.billDao().install(bill)
        install(image)
        return count
    }

    @Query("SELECT * FROM image WHERE image_id IN (:img_ids) AND synced !=1")
    fun findImage(img_ids: List<String>): MutableList<Image>


    @Query("SELECT * FROM image WHERE bill_id =:billID AND deleted =0")
    fun findByBillID(billID: String): MutableList<Image>

    @Query("DELETE FROM image WHERE bill_id =:billID")
    fun deleteBillImage(billID: String)

    @Query("SELECT image_id FROM image WHERE bill_id =:billID AND deleted !=1")
    fun findImagesId(billID: String): MutableList<String>

    @Transaction
    @Query("UPDATE image SET deleted = 1 WHERE image_id =:imageID")
    fun preDelete(imageID: String)

    @Transaction
    @Query("UPDATE image SET local_path=:imagePath, synced =:status WHERE image_id =:id")
    fun updateImageLocalPath(id: String, imagePath: String, status: Int): Int

    @Transaction
    @Query("UPDATE image SET online_path=:onlinePath, synced=:status  WHERE image_id =:imgId")
    fun updateOnlinePath(imgId: String, onlinePath: String, status: Int): Int

    @Query("SELECT * FROM image WHERE online_path=:path")
    fun findByOnLinePath(path: String): MutableList<Image>

    @Query("DELETE FROM ${Image.TAB_NAME} WHERE ${Image.COLUMN_ID}=:imgID")
    fun deleteById(imgID: String)

    @Query("SELECT * FROM image WHERE bill_id =:billId AND deleted !=1")
    fun observeByBillId(billId: String): Flow<MutableList<Image>>

    @Query("SELECT * FROM image WHERE bill_id =:billId AND deleted !=1")
    fun findByBillId(billId: String): MutableList<Image>

    @Query("SELECT * FROM image WHERE (local_path ISNULL OR local_path='') AND(online_path!='' OR online_path != NULL)")
    fun observerNotDownloadImages(): Flow<MutableList<Image>>

}