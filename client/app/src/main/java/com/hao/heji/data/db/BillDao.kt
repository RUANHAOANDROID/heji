package com.hao.heji.data.db

import androidx.room.*
import com.hao.heji.config.Config
import com.hao.heji.data.converters.MoneyConverters
import com.hao.heji.data.db.dto.*
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * @date: 2020/8/28
 * @author: 锅得铁
 * #
 */
@Dao
interface BillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun install(billTab: Bill): Long

    @Query("SELECT COUNT(1) FROM bill WHERE hash =:hasCode")
    fun exist(hasCode: Int): Int

    /**
     * 当有子表时慎用
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(billTab: Bill): Long

    @Update(entity = Bill::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(bill: Bill): Int

    @Query("SELECT * FROM bill WHERE bill_id=:billId")
    fun findById(billId: String): Bill

    @Query("SELECT sync_status FROM bill WHERE bill_id=:billId")
    fun status(billId: String): Int

    @Query("UPDATE bill SET sync_status =:status WHERE bill_id=:billId AND crt_user=:uid")
    fun preDelete(billId: String, uid: String, status: Int = STATUS.DELETED): Int

    @Query("UPDATE bill SET sync_status = :status WHERE bill_id=:billId")
    fun updateSyncStatus(billId: String, status: Int): Int

    @Query("DELETE FROM bill WHERE bill_id=:billId")
    fun deleteById(billId: String): Int

    @Query("DELETE FROM bill WHERE book_id=:id")
    fun deleteByBookId(id: String): Int

    @Query("SELECT count(*)  FROM bill WHERE bill_id =:id")
    fun countById(id: String): Int

    @Query("SELECT count(*)  FROM bill WHERE book_id =:bookId")
    fun countByBookId(bookId: String): Int

    @Query("SELECT * FROM bill WHERE book_id=:bookId AND sync_status !=:status LIMIT 100")
    fun flowNotSynced(bookId: String, status: Int = STATUS.SYNCED): Flow<MutableList<Bill>>

    /**
     * 根据时间区间查
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 账单列表
     */
    @Query("SELECT * FROM bill WHERE (date(time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND ($NOT_REDELETE) ORDER BY time DESC ,bill_id DESC")
    fun findBetweenTime(
        start: String,
        end: String,
        bookId: String? = Config.book.id
    ): MutableList<Bill>

    /**
     * 查询有账单的日子,日子去重
     *
     * @param start
     * @param end
     * @return
     */
    @Query("SELECT DISTINCT date(time)   FROM bill WHERE ( date(time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND ($NOT_REDELETE) ORDER BY time DESC ,bill_id DESC")
    fun findHaveBillDays(
        start: String,
        end: String,
        bookId: String? = Config.book.id
    ): MutableList<String>

    @Query("SELECT * FROM bill WHERE date(time) =:time AND book_id=:bookId AND sync_status!=-1")
    fun findByDay(time: String, bookId: String? = Config.book.id): MutableList<Bill>

    /**
     * 时间内收入、支出
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return
     */
    @TypeConverters(MoneyConverters::class)
    @Query("SELECT SUM(money) AS value FROM Bill WHERE ( date(time) BETWEEN :start AND :end ) AND (book_id=:bookId) AND (type = :sz) AND (sync_status !=  ${STATUS.DELETED})")
    fun findTotalMoneyByTime(
        start: String,
        end: String,
        sz: Int,
        bookId: String? = Config.book.id
    ): Flow<Double>

    @TypeConverters(MoneyConverters::class)
    @Query("SELECT sum(case when type=-1 then money else 0 end)as expenditure ,sum(case  when  type=1 then money else 0 end)AS income FROM bill  WHERE sync_status!=:status AND book_id=:bookId AND date(time)=:time")
    fun sumDayIncome(
        time: String,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): Income

    @TypeConverters(MoneyConverters::class)
    @Query("SELECT sum(case when type=-1 then money else 0 end)AS expenditure ,sum(case  when  type=1 then money else 0 end)AS income ,date(time) AS time FROM bill  WHERE sync_status!=:status AND book_id=:bookId AND strftime('%Y-%m',time)=:yearMonth GROUP BY date(time) ORDER BY time DESC ,bill_id DESC")
    fun findEveryDayIncomeByMonth(
        bookId: String? = Config.book.id,
        yearMonth: String,
        status: Int = STATUS.DELETED
    ): MutableList<IncomeTime>

    @TypeConverters(MoneyConverters::class)
    @Query("SELECT sum(case when type=-1 then money else 0 end)AS expenditure ,sum(case  when  type=1 then money else 0 end)AS income FROM bill  WHERE sync_status!=:status AND book_id=:bookId AND ( strftime('%Y-%m',time)=:yearMonth)")
    fun sumIncome(
        yearMonth: String,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): Flow<Income>

    @TypeConverters(MoneyConverters::class)
    @Query("SELECT sum(case when type=-1 then money else 0 end)AS expenditure ,sum(case  when  type=1 then money else 0 end)AS income FROM bill  WHERE sync_status!=:status AND book_id=:bookId AND ( strftime('%Y-%m',time)=:yearMonth)")
    fun sumMonthIncomeExpenditure(
        yearMonth: String,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): Income


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
    @Query("SELECT sum(money) AS money,type,date(time) AS time FROM bill WHERE strftime('%Y-%m',time) ==:date AND book_id=:bookId AND type=:type AND sync_status!=:status GROUP by date(time)")
    fun sumByMonth(
        date: String,
        type: Int,
        bookId: String = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<BillTotal>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',time) ==:date AND book_id=:bookId AND type=:type AND sync_status!=:status")
    fun findByMonth(
        date: String,
        type: Int?,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m-%d',time) ==:date AND book_id=:bookId AND sync_status!=:status AND type=:type order by date(time)")
    fun findByDay(
        date: String,
        type: Int,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',time) ==:date AND book_id=:bookId AND type=:type AND sync_status!=:status group by date(time)")
    fun findByMonthGroupByDay(
        date: String,
        type: Int,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<Bill>

    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',time) ==:date AND book_id=:bookId AND type =:type AND sync_status!=:status group by category")
    fun findByMonthGroupByCategory(
        date: String,
        type: Int,
        bookId: String = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<Bill>

    /**
     * 根据Category月份查询账单
     *
     * @param date
     * @return
     */
    @Query("SELECT * FROM bill WHERE strftime('%Y-%m',time) ==:date AND book_id=:bookId AND category=:category AND type =:type AND sync_status!=:status")
    fun findByCategoryAndMonth(
        category: String,
        date: String,
        type: Int,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<Bill>

    //---------------统计----------------//
    @TypeConverters(MoneyConverters::class)
    @Query(
        "SELECT strftime('%m-%d',time) AS time ," +
                "sum(case when type =-1 then money else 0 end) AS expenditure ," +
                " sum(case when type =1 then money else 0 end) AS income ," +
                " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) AS surplus" +
                " FROM bill WHERE strftime('%Y-%m',time) =:yearMonth AND book_id=:bookId AND sync_status!=:status GROUP BY strftime('%Y-%m-%d',time)"
    )
    fun listIncomeExpSurplusByMonth(
        yearMonth: String,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<IncomeTimeSurplus>

    @TypeConverters(MoneyConverters::class)
    @Query(
        "SELECT strftime('%Y-%m',time) AS time ," +
                " sum(case when type =-1 then money else 0 end) AS expenditure ," +
                " sum(case when type =1 then money else 0 end) AS income ," +
                " sum(case when type =1 then money else 0 end) - sum(case when type =-1 then money else 0 end) AS surplus" +
                " FROM bill WHERE strftime('%Y',time) =:year AND book_id=:bookId AND sync_status!=:status GROUP BY strftime('%Y-%m',time)"
    )
    fun listIncomeExpSurplusByYear(
        year: String,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): MutableList<IncomeTimeSurplus>
    /**
     * 根据月份查询账单
     *
     * @param date
     * @return
     */
    //    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ," +
    //            "sum(case  when  type=1 then money else 0 end)as income ," +
    //            "date(time) as time " +
    //            "from bill  where sync_status!=-1 AND strftime('%Y-%m',time) ==:date group by date(time) ")
    //    List<IncomeTimeSurplus> reportMonthList(String date);
    /**
     * 查询全年月份账单
     *
     * @param date
     * @return
     */
    //    @Query("select sum(case when type=-1 then money else 0 end)as expenditure ," +
    //            "sum(case  when  type=1 then money else 0 end)as income ," +
    //            "strftime('%Y-%m',time) as time ,avg(money) as surplus " +
    //            "from bill  where sync_status!=-1 AND strftime('%Y',time) ==:date group by strftime('%Y-%m',time) ")
    //    List<IncomeTimeSurplus> reportYearList(String date);
    @TypeConverters(MoneyConverters::class)
    @Query(
        "SELECT category AS category,sum(money)AS money," +
                "round(sum(money)*100.0 / (select sum(money)  from bill where book_id=:bookId and type =:type and sync_status!=-1 and strftime('%Y-%m',time) ==:date),2)AS percentage " +
                "FROM bill WHERE type =:type AND sync_status!=:status AND book_id=:bookId AND strftime('%Y-%m',time) ==:date GROUP BY category ORDER BY money DESC"
    )
    fun reportCategory(
        type: Int,
        date: String,
        bookId: String? = Config.book.id,
        status: Int = STATUS.DELETED
    ): List<CategoryPercentage>

    /**
     * @param bill
     */
    @Delete
    fun delete(bill: Bill)


}
