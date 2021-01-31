package com.rh.heji.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/**
 * Date: 2020/11/19
 * Author: 锅得铁
 * #
 */
@Dao
public interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void install(Image ticket);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void install(List<Image> ticket);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(List<Image> images);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Image image);

    @Delete
    void delete(Image ticket);

    @Transaction
    @Query("update bill_img set img_path=:imagePath, sync_status =:status where id =:id")
    int updateImageLocalPath(String id, String imagePath, int status);

    @Query("select * from bill_img where img_online_path=:path")
    List<Image> findByOnLinePath(String path);

    @Query("DELETE FROM " + Image.TAB_NAME + " WHERE " + Image.COLUMN_ID + "=:imgID")
    void deleteById(String imgID);

    @Query("SELECT * FROM bill_img WHERE bill_img_id =:billId")
    List<Image> findByBillId(String billId);

    @Query("SELECT * FROM bill_img WHERE bill_img_id =:billId AND sync_status==" + Constant.STATUS_NOT_SYNC)
    List<Image> findByBillImgIdNotAsync(String billId);

    @Query("SELECT * FROM bill_img WHERE (img_path ISNULL OR img_path=='') AND(img_online_path!='' OR img_online_path != NULL)")
    LiveData<List<Image>> observerNotDownloadImages();
}
