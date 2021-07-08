package com.rh.heji.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

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

    @Query("delete from bill_img where _bid =:billID")
    fun deleteBillImage(billID: String )

    @Transaction
    @Query("update bill_img set img_path=:imagePath, sync_status =:status where id =:id")
    fun updateImageLocalPath(id: String , imagePath: String , status: Int): Int

    @Transaction
    @Query("update bill_img set img_online_path=:onlinePath, sync_status=:status  where id =:imgId")
    fun updateOnlinePath(imgId: String , onlinePath: String , status: Int): Int

    @Query("select * from bill_img where img_online_path=:path")
    fun findByOnLinePath(path: String ): MutableList<Image >

    @Query("DELETE FROM " + Image.Companion.TAB_NAME + " WHERE " + Image.Companion.COLUMN_ID + "=:imgID")
    fun deleteById(imgID: String )

    @Query("SELECT * FROM bill_img WHERE _bid =:billId")
    fun findByBillId(billId: String ): MutableList<Image >

    @Query("SELECT * FROM bill_img WHERE id =:id")
    fun findById(id: String ): MutableList<Image >

    @Query("SELECT * FROM bill_img WHERE _bid =:billId AND sync_status==" + Constant.Companion.STATUS_NOT_SYNC)
    fun findByBillIdNotAsync(billId: String): MutableList<Image>

    @Query("SELECT * FROM bill_img WHERE (img_path ISNULL OR img_path=='') AND(img_online_path!='' OR img_online_path != NULL)")
    fun observerNotDownloadImages(): LiveData<MutableList<Image > >
}