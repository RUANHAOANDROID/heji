package com.heji.server.data.controller;

import com.alibaba.excel.EasyExcel;
import com.heji.server.data.Bill;
import com.heji.server.data.bean.QianjiExcelBean;
import com.heji.server.data.repository.BillDao;
import com.heji.server.exception.NotFindBillException;
import com.heji.server.file.StorageFileNotFoundException;
import com.heji.server.file.StorageService;
import com.heji.server.result.Result;
import com.heji.server.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
//@Controller // This means that this class is a Controller
@RestController//json controller
@RequestMapping(path = "/bill") // This means URL's start with /demo (after Application path)
public class BillController {
    final BillDao billDao;
    final StorageService storageService;

    public BillController(BillDao billDao, StorageService storageService) {
        this.billDao = billDao;
        this.storageService = storageService;
    }

    @ResponseBody
    @PostMapping(value = {"/add"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addBill(@RequestBody Bill bill) {
        bill.setCreateTime(System.currentTimeMillis());
        Bill dbBill = billDao.save(bill);
        return Result.success(dbBill.getUid());
    }

    @ResponseBody
    @PostMapping(value = {"/addBills"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addBill(@RequestBody List<Bill> bills) {
        if (null != bills && bills.size() > 0) {
            List<Bill> newBills = bills.stream().map(bill -> {
                bill.setCreateTime(System.currentTimeMillis());
                return bill;
            }).collect(Collectors.toList());
            billDao.saveAll(newBills);
            return Result.success("成功插入：" + newBills.size() + "条");
        }
        return Result.error("插入失败空的账单");
    }

    @ResponseBody
    @PostMapping(value = {"/update"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String update(@RequestBody Bill bill) {
        if (bill.getUpdateTime() == 0) {
            bill.setUpdateTime(System.currentTimeMillis());
        }
        billDao.save(bill);
        return Result.success("更新成功", bill.getUid());
    }

    @ResponseBody
    @GetMapping(value = {"/delete"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteById(@RequestParam String id) {
        Bill bill = billDao.findBillByUid(id);
        if (bill == null)
            return Result.error("该账单不存在");
        List<String> images = bill.getImages();
        if (null != images && images.size() > 0) {
            log.info("删除照片数量：{}个", images.size());
            images.forEach(image -> {
                try {
                    storageService.delete(image);
                    log.info("删除照片:{}", image);
                } catch (StorageFileNotFoundException e) {
                    log.info("照片{}不存在,{},{}", image, e.getMessage());
                }
            });
        }
        int count = billDao.deleteBillByUid(id);
        return Result.success("删除账单成功", count);
    }

    /**
     * @param startTime
     * @param endTime
     * @param type      收\支
     * @return
     */
    @ResponseBody
    @PostMapping(value = {"/getBills"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBills(@RequestParam(defaultValue = "0") long startTime, @RequestParam(defaultValue = "0") long endTime, @RequestParam(defaultValue = "0") int type) {
        List<Bill> bills;

        if (endTime == 0 || endTime == 0) {
            bills = billDao.findAll();
            return Result.success(bills);
        }
        if (type == 0) {
            bills = billDao.findBillByTimeBetween(startTime, endTime);
            return Result.success(bills);
        }
        bills = billDao.findAllByTypeAndTimeBetween(type, startTime, endTime);
        return Result.success(bills);
    }

    @PostMapping("/export")
    @ResponseBody
    public ResponseEntity<Resource> exportBills(@RequestParam(defaultValue = "0") String year, @RequestParam(defaultValue = "0") String month) {
        List<Bill> bills = billDao.findAll();
        if (bills.isEmpty())
            throw new NotFindBillException("No bill");
        //bill转换成QianJi相应的格式
        List<QianjiExcelBean> data = bills.stream().map(bill -> {
            QianjiExcelBean excel = new QianjiExcelBean();
            excel.setTime(TimeUtils.millis2String(bill.getTime(), "yyyy/MM/dd HH:mm:ss"));
            excel.setMoney(bill.getMoney());
            excel.setRemark(bill.getRemark());
            excel.setType(bill.getType() == 1 ? "收入" : "支出");
            excel.setCategory(bill.getCategory());
            return excel;
        }).collect(Collectors.toList());
        SimpleDateFormat dateFormat = TimeUtils.getDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = TimeUtils.getNowString(dateFormat) + ".xlsx";
        String filePath = storageService.load(filename).toString();
        EasyExcel.write(filePath, QianjiExcelBean.class).sheet("账单模板").doWrite(data);
        Resource file = storageService.loadAsResource(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename())
                .header(HttpHeaders.CONTENT_TYPE, "application/file")
                .body(file);
    }

    @PostMapping("/input/{sources}")
    @ResponseBody
    public String inputBills(@PathVariable String source, @RequestParam("file") MultipartFile file) {
        switch (source) {
            case "zhifubao":

                break;
            case "weixin":
                break;
            case "qianji":
                break;
            default:
                break;
        }
        return Result.success("导入成功");
    }
}
