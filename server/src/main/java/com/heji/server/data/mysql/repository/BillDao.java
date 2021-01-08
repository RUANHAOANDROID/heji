package com.heji.server.data.mysql.repository;

import com.heji.server.data.mysql.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BillDao extends JpaRepository<Bill, Integer> {
    /**
     * 查所有 ，更具月份查
     *
     * @param start
     * @param end
     * @return
     */
    @Query
    List<Bill> findAllByTimeBetween(long start, long end);

    /**
     * 查所有收支
     * @param type 收入支出
     * @return
     */
    @Query
    List<Bill> findAllByType(int type);

    /**
     * 查时间内 收支
     *
     * @param type
     * @param start
     * @param end
     * @return
     */
    @Query
    List<Bill> findAllByTypeAndTimeBetween(int type, long start, long end);
    /**
     * 查时间内
     *
     * @param start
     * @param end
     * @return
     */
    @Query
    List<Bill> findBillByTimeBetween(long start, long end);

    /**
     *
     * @param billID
     * @return
     */
    @Query
    Bill findBillByUid(String billID);

    @Transactional
    @Query
    Integer deleteBillByUid(String id);
}
