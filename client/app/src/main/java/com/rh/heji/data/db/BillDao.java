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
import com.rh.heji.data.db.query.CategoryPercentage;
import com.rh.heji.data.db.query.Income;
import com.rh.heji.data.db.query.IncomeTime;
import com.rh.heji.data.db.query.IncomeTimeSurplus;

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
    List<String> findIds(Date time, BigDecimal money, String remark);

    @Query("select count(*)  from bill where bill_id =:id")
    int countById(String id);

    /**
     * @param syncStatus 同步状态
     * @return
     */
    @Query("SELECT bill_id FROM bill WHERE sync_status =:syncStatus")
    LiveData<List<String>> observeSyncStatus(int syncStatus);


    /**
     * 根据时间区间查
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 账单列表
     */
    @Query("SELECT * FROM bill WHERE (date(bill_time) BETWEEN :start AND :end ) AND (sync_status !=" + Constant.STATUS_DELETE + ") ORDER BY bill_time DESC ,bill_id DESC")
    List<Bill> findBetweenTime(String start, String end);

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
    List<Bill> findByDay(String time);


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
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income ,date(bill_time) as time from bill  where sync_status!=-1 AND date(bill_time)BETWEEN:startTime and :endTime group by date(bill_time) ORDER BY bill_time DESC ,bill_id DESC")
    List<IncomeTime> findEveryDayIncome(String startTime, String endTime);

    @TypeConverters(MoneyConverters.class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND ( date(bill_time) BETWEEN :start AND :end )")
    LiveData<Income> sumIncome(String start, String end);

    @TypeConverters(MoneyConverters.class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND ( strftime('%Y-%m',bill_time)=:yearMonth)")
    Income sumMonthIncomeExpenditure(String yearMonth);

    @Transaction
    @Query("SELECT * FROM bill")
    List<BillWithImage> findAllBillWhitImage();

    @Transaction
    @Query("SELECT * FROM bill WHERE img_count > 0 AND sync_status==" + Constant.STATUS_NOT_SYNC)
    List<BillWithImage> findNotSyncBillWhitImage();

    @Query("SELECT * FROM bill WHERE  sync_status==:syncStatus")
    List<Bill> findByStatus(int syncStatus);

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND sync_status!=" + Constant.STATUS_DELETE+" group by date(bill_time)")
    List<Bill> findByMonth(String date);

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND type=:type AND sync_status!=" + Constant.STATUS_DELETE+" group by date(bill_time)")
    List<Bill> findByMonthGroupByDay(String date,int type);
    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND type =:type AND sync_status!=" + Constant.STATUS_DELETE+" group by category")
    List<Bill> findByMonthGroupByCategory(String date,int type);

    //---------------统计----------------//


    @TypeConverters(MoneyConverters.class)
    @Query("select strftime('%m-%d',bill_time) as time ," +
            "sum(case when type =-1 then money else 0 end) as expenditure ," +
            " sum(case when type =1 then money else 0 end) as income ,"+
            " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) as surplus"+
            " from bill where strftime('%Y-%m',bill_time) =:yearMonth group by strftime('%Y-%m-%d',bill_time)")
    List<IncomeTimeSurplus> listIncomeExpSurplusByMonth(String yearMonth);


    @TypeConverters(MoneyConverters.class)
    @Query("select strftime('%Y-%m',bill_time) as time ," +
            " sum(case when type =-1 then money else 0 end) as expenditure ," +
            " sum(case when type =1 then money else 0 end) as income ," +
            " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) as surplus"+
            " from bill where strftime('%Y',bill_time) =:year group by strftime('%Y-%m',bill_time)")
    List<IncomeTimeSurplus> listIncomeExpSurplusByYear(String year);

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
//    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ," +
//            "sum(case  when  type=1 then money else 0 end)as income ," +
//            "date(bill_time) as time " +
//            "from bill  where sync_status!=-1 AND strftime('%Y-%m',bill_time) ==:date group by date(bill_time) ")
//    List<IncomeTimeSurplus> reportMonthList(String date);

    /**
     * 查询全年月份账单
     *
     * @param date
     * @return
     */
//    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ," +
//            "sum(case  when  type=1 then money else 0 end)as income ," +
//            "strftime('%Y-%m',bill_time) as time ,avg(money) as surplus " +
//            "from bill  where sync_status!=-1 AND strftime('%Y',bill_time) ==:date group by strftime('%Y-%m',bill_time) ")
//    List<IncomeTimeSurplus> reportYearList(String date);
    @TypeConverters(MoneyConverters.class)
    @Query("select category as category,sum(money)as money," +
            "round(sum(money)*100.0 / (select sum(money)  from bill where type =:type and strftime('%Y-%m',bill_time) ==:date),2)as percentage " +
            "from bill where type =:type and sync_status!=-1 and strftime('%Y-%m',bill_time) ==:date group by category")
    List<CategoryPercentage> reportCategory(int type, String date);

    /**
     * @param bill
     */
    @Delete
    void delete(Bill bill);

}
