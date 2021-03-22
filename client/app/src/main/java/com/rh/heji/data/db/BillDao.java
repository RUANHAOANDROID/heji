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
import com.rh.heji.data.db.query.Income;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@Dao
public interface BillDao {
    String REDELETE = "sync_status!=" + Constant.STATUS_DELETE;

    @Update(onConflict = REPLACE)
    int update(Bill bill);

    @Insert(onConflict = REPLACE)
    long install(Bill billTab);

    @Query("update bill set sync_status = " + Constant.STATUS_DELETE + " where bill_id=:billId")
    int preDelete(String billId);

    @Transaction
    @Query("update bill set img_count=:count where bill_id=:id")
    int updateImageCount(int count, String id);

    /**
     * 根据精确时间datetime(),money,remark查询
     *
     * @param time
     * @param money
     * @param remark
     * @return
     */
    @TypeConverters({MoneyConverters.class, DateConverters.class})
    @Query("select bill_id from bill where datetime(bill_time) =:time and money =:money and remark=:remark")
    List<String> findBill(Date time, BigDecimal money, String remark);

    @Query("select * from bill where bill_id =:id")
    List<Bill> findByID(String id);

    /**
     * @param syncStatus 同步状态
     * @return
     */
    @Query("SELECT bill_id FROM bill WHERE sync_status =:syncStatus")
    LiveData<List<String>> observeSyncStatus(int syncStatus);

    /**
     * 根据时间区间查
     * Flowable的被观察对象使用Consumer观察员
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 账单列表
     */

    @Query("SELECT * FROM bill WHERE (date(bill_time) BETWEEN :start AND :end ) AND (sync_status !=" + Constant.STATUS_DELETE + ") ORDER BY bill_time DESC ,bill_id DESC")
    LiveData<List<Bill>> findBillsFollowableByTime(String start, String end);

    /**
     * 根据时间区间查
     * @param start 起始时间
     * @param end   结束时间
     * @return 账单列表
     */
    @Query("SELECT * FROM bill WHERE (date(bill_time) BETWEEN :start AND :end ) AND (sync_status !=" + Constant.STATUS_DELETE + ") ORDER BY bill_time DESC ,bill_id DESC")
    List<Bill> findListBetweenTime(String start, String end);

    /**
     * 查询有账单的日子,日子去重
     *
     * @param start
     * @param end
     * @return
     */
    @Query("SELECT DISTINCT date(bill_time)   FROM bill WHERE ( date(bill_time) BETWEEN :start AND :end ) AND (" + REDELETE + ") ORDER BY bill_time DESC ,bill_id DESC")
    List<String> findHaveBillDays(String start, String end);

    @Query("SELECT * FROM bill WHERE date(bill_time) =:time AND sync_status!=-1")
    List<Bill> findListByDay(String time);

    /**
     * 时间内收入、支出
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return
     */
    @TypeConverters(MoneyConverters.class)
    @Query("SELECT SUM(money) AS value FROM Bill WHERE ( date(bill_time) BETWEEN :start AND :end ) AND (type = :sz) AND (sync_status != " + Constant.STATUS_DELETE + ")")
    LiveData<Double> findTotalMoneyByTime(String start, String end, int sz);

    @TypeConverters(MoneyConverters.class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND date(bill_time)=:time")
    Income sumDayIncome(String time);

    @TypeConverters(MoneyConverters.class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income ,date(bill_time) as time from bill  where sync_status!=-1 AND date(bill_time)BETWEEN:startTime and :endTime group by date(bill_time) ")
    List<Income> findEveryDayIncome(String startTime, String endTime);

    @TypeConverters(MoneyConverters.class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND ( date(bill_time) BETWEEN :start AND :end )")
    LiveData<Income> sumIncome(String start, String end);

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
