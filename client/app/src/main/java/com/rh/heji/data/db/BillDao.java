package com.rh.heji.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.rh.heji.data.converters.DateConverters;
import com.rh.heji.data.converters.MoneyConverters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@Dao
public interface BillDao {

    @Update(onConflict = REPLACE)
    int update(Bill bill);

    @Insert(onConflict = REPLACE)
    long install(Bill billTab);


    @Transaction
    @Query("update bill set img_count=:count where bill_id=:id")
    int updateImageCount(int count, String id);

    @TypeConverters({MoneyConverters.class, DateConverters.class})
    @Query("select bill_id from bill where bill_time =:time and money =:money and remark=:remark")
    List<String> findBill(Date time, BigDecimal money, String remark);

    @Query("select * from bill where bill_id =:id and sync_status =:syncStatus")
    List<Bill> findBillByIdAndSyncStatus(String id, int syncStatus);

    @Query("select * from bill where bill_id =:id")
    List<Bill> findByBillId(String id);

    /**
     * @param syncStatus 同步状态
     * @return
     */
    @Query("SELECT bill_id FROM bill WHERE sync_status =:syncStatus")
    LiveData<List<String>> observeSyncStatus(int syncStatus);

    /**
     * 根据记账时间——次要的
     *
     * @param monthly
     * @return
     */
    @Query("select * from bill where strftime('%m',create_time)=:monthly;")
    LiveData<List<Bill>> queryAllByCreateTime(String monthly);

    /**
     * 根据时间区间查
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return
     */
    @Query("SELECT * FROM bill WHERE (bill_time BETWEEN :start AND :end) AND (sync_status !=" + Constant.STATUS_DELETE + ") ORDER BY bill_time,create_time DESC")
    LiveData<List<Bill>> findBillsByTime(String start, String end);

    /**
     * 根据时间区间查
     * Flowable的被观察对象使用Consumer观察员
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 账单列表
     */

    @Query("SELECT * FROM bill WHERE (bill_time BETWEEN :start AND :end) AND (sync_status !=" + Constant.STATUS_DELETE + ") ORDER BY bill_time DESC ,bill_id DESC")
    Flowable<List<Bill>> findBillsFollowableByTime(String start, String end);

    @Query("SELECT * FROM bill WHERE (bill_time BETWEEN :start AND :end) AND (sync_status !=" + Constant.STATUS_DELETE + ") ORDER BY bill_time DESC ,bill_id DESC")
    List<Bill> findBillsBetweenTime(String start, String end);

    @Query("SELECT SUM(money/100) FROM bill WHERE date(bill_time) =:time AND type =:type AND sync_status!=-1")
    String findDayIncome(String time, String type);

    /**
     * 时间内收入、支出
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return
     */
    @Query("SELECT SUM(money) AS value FROM Bill WHERE (bill_time BETWEEN :start AND :end )AND (type =:sz) AND (sync_status != " + Constant.STATUS_DELETE + ")")
    LiveData<Double> findTotalMoneyByTime(String start, String end, int sz);

    @Transaction
    @Query("SELECT * FROM bill")
    List<BillWithImage> findAllBillWhitImage();

    @Transaction
    @Query("SELECT * FROM bill WHERE img_count > 0 AND sync_status==" + Constant.STATUS_NOT_SYNC)
    List<BillWithImage> findNotSyncBillWhitImage();

    @Query("SELECT * FROM bill WHERE  sync_status==:syncStatus")
    List<Bill> findBillsByStatus(int syncStatus);

    /**
     * @param bill
     */
    @Delete
    void delete(Bill bill);

}
