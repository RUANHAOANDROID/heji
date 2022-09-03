package com.rh.heji.data.db

import androidx.room.*
import com.rh.heji.App
import kotlinx.coroutines.flow.Flow

/**
 * Date: 2020/11/19
 * @author: 锅得铁
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
        var count =App.dataBase.billDao().install(bill)
        install(image)
        return count
    }

    @Query("SELECT * FROM image WHERE image_id IN (:img_ids) AND sync_status !=${STATUS.DELETED}")
    fun findImage(img_ids:List<String>):MutableList<Image>


    @Query("SELECT * FROM image WHERE bill_id =:billID AND sync_status =:status")
    fun findByBillID(billID:String,status:Int):MutableList<Image>

    @Query("DELETE FROM image WHERE bill_id =:billID")
    fun deleteBillImage(billID: String )

    @Query("SELECT image_id FROM image WHERE bill_id =:billID AND sync_status !=${STATUS.DELETED}")
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

    @Query("SELECT * FROM image WHERE bill_id =:billId AND sync_status !=${STATUS.DELETED}")
    fun findByBillId(billId: String ): Flow<MutableList<Image>>

    @Query("SELECT * FROM image WHERE (local_path ISNULL OR local_path='') AND(online_path!='' OR online_path != NULL)")
    fun observerNotDownloadImages(): Flow<MutableList<Image>>

}