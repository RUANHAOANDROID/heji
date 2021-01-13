package com.heji.server.service;

import com.heji.server.data.mongo.MBill;
import com.heji.server.module.BillModule;

import java.util.List;

public interface BillService {
    String addBill(BillModule billModule);

    boolean removeBill(String _id);

    MBill updateBill(MBill bill);

    String upInstImages(String _id, String[] images);

    List<MBill> getBills(String year, String month);

    MBill getBillInfo(String billId);

    boolean exists(String _id);

    void sumBills();

    String sumBills(String year, String month);

    void avgBills();
}
