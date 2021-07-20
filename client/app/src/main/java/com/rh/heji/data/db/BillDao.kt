package com.rh.heji.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rh.heji.AppCache
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.query.CategoryPercentage
import com.rh.heji.data.db.query.Income
import com.rh.heji.data.db.query.IncomeTime
import com.rh.heji.data.db.query.IncomeTimeSurplus
import java.math.BigDecimal
import java.util.*

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@Dao
interface BillDao {

    val bookId: String
        get() = AppCache.getInstance().currentBook.id

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(bill: Bill): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(billTab: Bill): Long

    @Query("update bill set sync_status = $STATUS_DELETE where id=:billId")
    fun preDelete(billId: String): Int

    @Transaction
    @Query("update bill set img_count=:count where id=:id")
    fun updateImageCount(count: Int, id: String): Int

    /**
     * 根据精确时间datetime(),money,remark查询
     *
     * @param time
     * @param money
     * @param remark
     * @return
     */
    @TypeConverters(MoneyConverters::class, DateConverters::class)
    @Query("select id from bill where datetime(bill_time) =:time AND money =:money AND remark=:remark AND book_id=:bookId")
    fun findIds(time: Date, money: BigDecimal, remark: String,bookId: String=AppCache.getInstance().currentBook.id): MutableList<String>

    @Query("select count(*)  from bill where id =:id")
    fun countById(id: String): Int

    /**
     * @param syncStatus 同步状态
     * @return
     */
    @Query("SELECT id FROM bill WHERE sync_status =:syncStatus")
    fun observeSyncStatus(syncStatus: Int): LiveData<MutableList<String>>

    /**
     * 根据时间区间查
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 账单列表
     */
    @Query("SELECT * FROM bill WHERE (date(bill_time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND ($NOT_REDELETE) ORDER BY bill_time DESC ,id DESC")
    fun findBetweenTime(start: String, end: String,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<Bill>

    /**
     * 查询有账单的日子,日子去重
     *
     * @param start
     * @param end
     * @return
     */
    @Query("SELECT DISTINCT date(bill_time)   FROM bill WHERE ( date(bill_time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND ($NOT_REDELETE) ORDER BY bill_time DESC ,id DESC")
    fun findHaveBillDays(start: String, end: String,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<String>

    @Query("SELECT * FROM bill WHERE date(bill_time) =:time AND book_id=:bookId AND sync_status!=-1")
    fun findByDay(time: String,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<Bill>

    /**
     * 时间内收入、支出
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return
     */
    @TypeConverters(MoneyConverters::class)
    @Query("SELECT SUM(money) AS value FROM Bill WHERE ( date(bill_time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND (type = :sz) AND (sync_status != $STATUS_DELETE)")
    fun findTotalMoneyByTime(start: String, end: String, sz: Int,bookId: String?=AppCache.getInstance().currentBook.id): LiveData<Double>

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND book_id=:bookId AND date(bill_time)=:time")
    fun sumDayIncome(time: String,bookId: String?=AppCache.getInstance().currentBook.id): Income

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income ,date(bill_time) as time from bill  where sync_status!=-1 AND book_id=:bookId AND strftime('%Y-%m',bill_time)=:yearMonth group by date(bill_time) ORDER BY bill_time DESC ,id DESC")
    fun findEveryDayIncomeByMonth(bookId: String?, yearMonth: String): MutableList<IncomeTime>

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND book_id=:bookId AND ( strftime('%Y-%m',bill_time)=:yearMonth)")
    fun sumIncome(yearMonth: String,bookId: String?=AppCache.getInstance().currentBook.id): LiveData<Income>

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND book_id=:bookId AND ( strftime('%Y-%m',bill_time)=:yearMonth)")
    fun sumMonthIncomeExpenditure(yearMonth: String,bookId: String?=AppCache.getInstance().currentBook.id): Income

    @Transaction
    @Query("SELECT * FROM bill")
    fun findAllBillWhitImage(): MutableList<BillWithImage>

    @Transaction
    @Query("SELECT * FROM bill WHERE img_count > 0 AND sync_status==$STATUS_NOT_SYNC")
    fun findNotSyncBillWhitImage(): MutableList<BillWithImage>

    @Query("SELECT * FROM bill WHERE  sync_status==:syncStatus")
    fun findByStatus(syncStatus: Int): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND sync_status!=$STATUS_DELETE group by date(bill_time)")
    fun findByMonth(date: String,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m-%d',bill_time) ==:date AND book_id=:bookId AND sync_status!=$STATUS_DELETE AND type=:type order by date(bill_time)")
    fun findByDay(date: String, type: Int,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND type=:type AND sync_status!=$STATUS_DELETE group by date(bill_time)")
    fun findByMonthGroupByDay(date: String, type: Int,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND type =:type AND sync_status!=$STATUS_DELETE group by category")
    fun findByMonthGroupByCategory(date: String, type: Int,bookId: String=AppCache.getInstance().currentBook.id): MutableList<Bill>

    /**
     * 根据Category月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND category=:category AND type =:type AND sync_status!=$STATUS_DELETE")
    fun findByCategoryAndMonth(category: String, date: String, type: Int,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<Bill>

    //---------------统计----------------//
    @TypeConverters(MoneyConverters::class)
    @Query(
        "select strftime('%m-%d',bill_time) as time ," +
                "sum(case when type =-1 then money else 0 end) as expenditure ," +
                " sum(case when type =1 then money else 0 end) as income ," +
                " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) as surplus" +
                " from bill where strftime('%Y-%m',bill_time) =:yearMonth AND book_id=:bookId AND sync_status!=-1 group by strftime('%Y-%m-%d',bill_time)"
    )
    fun listIncomeExpSurplusByMonth(yearMonth: String,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<IncomeTimeSurplus>

    @TypeConverters(MoneyConverters::class)
    @Query(
        "select strftime('%Y-%m',bill_time) as time ," +
                " sum(case when type =-1 then money else 0 end) as expenditure ," +
                " sum(case when type =1 then money else 0 end) as income ," +
                " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) as surplus" +
                " from bill where strftime('%Y',bill_time) =:year AND book_id=:bookId AND sync_status!=-1 group by strftime('%Y-%m',bill_time)"
    )
    fun listIncomeExpSurplusByYear(year: String,bookId: String?=AppCache.getInstance().currentBook.id): MutableList<IncomeTimeSurplus>
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
    @TypeConverters(MoneyConverters::class)
    @Query(
        "select category as category,sum(money)as money," +
                "round(sum(money)*100.0 / (select sum(money)  from bill where type =:type and strftime('%Y-%m',bill_time) ==:date),2)as percentage " +
                "from bill where type =:type and sync_status!=-1 AND book_id=:bookId AND strftime('%Y-%m',bill_time) ==:date group by category order by money desc"
    )
    fun reportCategory(type: Int, date: String,bookId: String?=AppCache.getInstance().currentBook.id): List<CategoryPercentage>

    /**
     * @param bill
     */
    @Delete
    fun delete(bill: Bill)


}
