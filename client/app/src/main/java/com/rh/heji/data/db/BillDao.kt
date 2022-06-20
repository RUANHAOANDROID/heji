package com.rh.heji.data.db

import androidx.room.*
import com.rh.heji.App
import com.rh.heji.App.Companion.currentBook
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.dto.*
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal
import java.util.*

/**
 * Date: 2020/8/28
 * @author: 锅得铁
 * #
 */
@Dao
interface BillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(billTab: Bill): Long

    @NotNull
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(billTab: Bill): Long

    @NotNull
    @Update(entity =Bill::class ,onConflict = OnConflictStrategy.REPLACE)
    fun update(bill: Bill): Int

    @Query("select sync_status from bill where bill_id=:billId")
    fun status(billId: String): Int

    @Query("update bill set sync_status = ${STATUS.DELETED} where bill_id=:billId")
    fun preDelete(billId: String): Int

    @Query("update bill set sync_status = :status where bill_id=:billId")
    fun updateSyncStatus(billId: String,status:Int): Int

    @Query("select bill_id from bill where book_id=:bookId")
    fun idsDeleted(bookId: String): Flow<List<String>>

    @Query("delete from bill where bill_id=:billId")
    fun deleteById(billId: String): Int

    @Query("delete from bill where book_id=:id")
    fun deleteByBookId(id: String): Int

    /**
     * 根据精确时间datetime(),money,remark查询
     *
     * @param time
     * @param money
     * @param remark
     * @return
     */
    @TypeConverters(MoneyConverters::class, DateConverters::class)
    @Query("select bill_id from bill where datetime(bill_time) =:time AND money =:money AND remark=:remark AND book_id=:bookId")
    fun findIds(
        time: Date,
        money: BigDecimal,
        remark: String,
        bookId: String = App. currentBook.id
    ): MutableList<String>

    @Query("select count(*)  from bill where bill_id =:id")
    fun countById(id: String): Int

    @Query("select count(*)  from bill where book_id =:bookId")
    fun countByBookId(bookId: String): Int

    /**
     * @param syncStatus 同步状态
     * @return
     */
    @Query("SELECT bill_id FROM bill WHERE sync_status =:syncStatus")
    fun observeSyncStatus(syncStatus: Int): Flow<MutableList<String>>

    /**
     * 根据时间区间查
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 账单列表
     */
    @Query("SELECT * FROM bill WHERE (date(bill_time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND ($NOT_REDELETE) ORDER BY bill_time DESC ,bill_id DESC")
    fun findBetweenTime(
        start: String,
        end: String,
        bookId: String? = currentBook.id
    ): MutableList<Bill>

    /**
     * 查询有账单的日子,日子去重
     *
     * @param start
     * @param end
     * @return
     */
    @Query("SELECT DISTINCT date(bill_time)   FROM bill WHERE ( date(bill_time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND ($NOT_REDELETE) ORDER BY bill_time DESC ,bill_id DESC")
    fun findHaveBillDays(
        start: String,
        end: String,
        bookId: String? = currentBook.id
    ): MutableList<String>

    @Query("SELECT * FROM bill WHERE date(bill_time) =:time AND book_id=:bookId AND sync_status!=-1")
    fun findByDay(time: String, bookId: String? = currentBook.id): MutableList<Bill>

    /**
     * 时间内收入、支出
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return
     */
    @TypeConverters(MoneyConverters::class)
    @Query("SELECT SUM(money) AS value FROM Bill WHERE ( date(bill_time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND (type = :sz) AND (sync_status !=  ${STATUS.DELETED})")
    fun findTotalMoneyByTime(
        start: String,
        end: String,
        sz: Int,
        bookId: String? = currentBook.id
    ): Flow<Double>

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND book_id=:bookId AND date(bill_time)=:time")
    fun sumDayIncome(time: String, bookId: String? = currentBook.id): Income

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income ,date(bill_time) as time from bill  where sync_status!=-1 AND book_id=:bookId AND strftime('%Y-%m',bill_time)=:yearMonth group by date(bill_time) ORDER BY bill_time DESC ,bill_id DESC")
    fun findEveryDayIncomeByMonth(bookId: String?= currentBook.id, yearMonth: String): MutableList<IncomeTime>

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND book_id=:bookId AND ( strftime('%Y-%m',bill_time)=:yearMonth)")
    fun sumIncome(yearMonth: String, bookId: String? = currentBook.id): Flow<Income>

    @TypeConverters(MoneyConverters::class)
    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)as income from bill  where sync_status!=-1 AND book_id=:bookId AND ( strftime('%Y-%m',bill_time)=:yearMonth)")
    fun sumMonthIncomeExpenditure(yearMonth: String, bookId: String? = currentBook.id): Income


    @Query("SELECT * FROM bill WHERE  sync_status==:syncStatus")
    fun findByStatus(syncStatus: Int): MutableList<Bill>
    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @TypeConverters(MoneyConverters::class)
    @Transaction
    @Query("SELECT sum(money) AS money,type,date(bill_time) AS time FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND type=:type AND sync_status!= ${STATUS.DELETED} group by date(bill_time)")
    fun sumByMonth(date: String, type: Int, bookId: String = currentBook.id): MutableList<BillTotal>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND type=:type AND sync_status!= ${STATUS.DELETED}")
    fun findByMonth(date: String, type: Int?, bookId: String? = currentBook.id): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m-%d',bill_time) ==:date AND book_id=:bookId AND sync_status!= ${STATUS.DELETED} AND type=:type order by date(bill_time)")
    fun findByDay(date: String, type: Int, bookId: String? = currentBook.id): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND type=:type AND sync_status!= ${STATUS.DELETED} group by date(bill_time)")
    fun findByMonthGroupByDay(
        date: String,
        type: Int,
        bookId: String? = currentBook.id
    ): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND type =:type AND sync_status!= ${STATUS.DELETED} group by category")
    fun findByMonthGroupByCategory(
        date: String,
        type: Int,
        bookId: String = currentBook.id
    ): MutableList<Bill>

    /**
     * 根据Category月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',bill_time) ==:date AND book_id=:bookId AND category=:category AND type =:type AND sync_status!= ${STATUS.DELETED}")
    fun findByCategoryAndMonth(
        category: String,
        date: String,
        type: Int,
        bookId: String? = currentBook.id
    ): MutableList<Bill>

    //---------------统计----------------//
    @TypeConverters(MoneyConverters::class)
    @Query(
        "select strftime('%m-%d',bill_time) as time ," +
                "sum(case when type =-1 then money else 0 end) as expenditure ," +
                " sum(case when type =1 then money else 0 end) as income ," +
                " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) as surplus" +
                " from bill where strftime('%Y-%m',bill_time) =:yearMonth AND book_id=:bookId AND sync_status!=-1 group by strftime('%Y-%m-%d',bill_time)"
    )
    fun listIncomeExpSurplusByMonth(
        yearMonth: String,
        bookId: String? = currentBook.id
    ): MutableList<IncomeTimeSurplus>

    @TypeConverters(MoneyConverters::class)
    @Query(
        "select strftime('%Y-%m',bill_time) as time ," +
                " sum(case when type =-1 then money else 0 end) as expenditure ," +
                " sum(case when type =1 then money else 0 end) as income ," +
                " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) as surplus" +
                " from bill where strftime('%Y',bill_time) =:year AND book_id=:bookId AND sync_status!=-1 group by strftime('%Y-%m',bill_time)"
    )
    fun listIncomeExpSurplusByYear(
        year: String,
        bookId: String? = currentBook.id
    ): MutableList<IncomeTimeSurplus>
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
                "round(sum(money)*100.0 / (select sum(money)  from bill where book_id=:bookId and type =:type and sync_status!=-1 and strftime('%Y-%m',bill_time) ==:date),2)as percentage " +
                "from bill where type =:type and sync_status!=-1 AND book_id=:bookId AND strftime('%Y-%m',bill_time) ==:date group by category order by money desc"
    )
    fun reportCategory(
        type: Int,
        date: String,
        bookId: String? = currentBook.id
    ): List<CategoryPercentage>

    /**
     * @param bill
     */
    @Delete
    fun delete(bill: Bill)


}
