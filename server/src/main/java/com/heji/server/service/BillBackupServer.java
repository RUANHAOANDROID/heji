package com.heji.server.service;

import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mongo.MBillBackup;

import java.util.List;

/**
 * 目前仅仅备份已经删除的账单
 */
public interface BillBackupServer {

    void backup(MBillBackup bill);

    MBill getAllBacks(String book_id);

    List<String> getAllBacksId(String book_id);

}
