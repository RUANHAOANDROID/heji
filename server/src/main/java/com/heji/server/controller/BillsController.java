package com.heji.server.controller;

import com.alibaba.excel.EasyExcel;
import com.heji.server.data.bean.QianjiExcelBean;
import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mysql.Bill;
import com.heji.server.exception.NotFindBillException;
import com.heji.server.file.StorageService;
import com.heji.server.module.BillModule;
import com.heji.server.result.Result;
import com.heji.server.service.BillService;
import com.heji.server.service.ImageService;
import com.heji.server.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
//@Controller // This means that this class is a Controller
@RestController//json controller
@RequestMapping(path = "/bill") // This means URL's start with /demo (after Application path)
public class BillsController {
    //账单
    final BillService billService;
    //本地存储
    final StorageService storageService;

    //账单照片存储
    final ImageService imageService;

    public BillsController(BillService billService, StorageService storageService, ImageService imageService) {
        this.billService = billService;
        this.storageService = storageService;
        this.imageService = imageService;
    }

    @ResponseBody
    @PostMapping(value = {"/add"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addBill(@RequestBody BillModule bill) {
        String billID = billService.addBill(bill);
        return Result.success(billID);
    }

    @ResponseBody
    @GetMapping(value = {"/info"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBills(String billId) {
        MBill bills = billService.getBillInfo(billId);
        return Result.success(bills);
    }


    @ResponseBody
    @GetMapping(value = {"delete"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteById(String billId) {
        imageService.removeBillImages(billId);
        billService.removeBill(billId);
        return Result.success("删除成功");
    }

    @ResponseBody
    @GetMapping(value = {"update"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String updateBill(MBill bill) {
        billService.updateBill(bill);
        return Result.success(bill.get_id());
    }

    @PostMapping("/export")
    @ResponseBody
    public ResponseEntity<Resource> exportBills(@RequestParam(defaultValue = "0") String year, @RequestParam(defaultValue = "0") String month) {
        List<MBill> bills = billService.getBills(year, month);
        if (Objects.isNull(bills) && bills.size() <= 0)
            throw new NotFindBillException("No bill");
        List<QianjiExcelBean> excelData = bill2QianJiExcel(bills);
        SimpleDateFormat dateFormat = TimeUtils.getDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = TimeUtils.getNowString(dateFormat) + ".xlsx";
        String filePath = storageService.load(filename).toString();
        EasyExcel.write(filePath, QianjiExcelBean.class).sheet("账单模板").doWrite(excelData);
        Resource file = storageService.loadAsResource(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename())
                .header(HttpHeaders.CONTENT_TYPE, "application/file")
                .body(file);
    }

    /**
     * 转换成QiJi需要的格式
     */
    private List<QianjiExcelBean> bill2QianJiExcel(List<MBill> bills) {
        //bill转换成QianJi相应的格式
        return bills.stream()
                .map(bill -> {
                    QianjiExcelBean excel = new QianjiExcelBean();
                    excel.setTime(TimeUtils.millis2String(bill.getTime(), "yyyy/MM/dd HH:mm:ss"));
                    excel.setMoney(String.valueOf(bill.getMoney()));
                    excel.setRemark(bill.getRemark());
                    excel.setType(bill.getType() == 1 ? "收入" : "支出");
                    excel.setCategory(bill.getCategory());
                    return excel;
                }).collect(Collectors.toList());
    }

}
