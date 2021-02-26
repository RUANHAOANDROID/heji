package com.heji.server.controller;

import com.alibaba.excel.EasyExcel;
import com.heji.server.data.bean.QianjiExcelBean;
import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mongo.MBillBackup;
import com.heji.server.exception.NotFindBillException;
import com.heji.server.file.StorageService;
import com.heji.server.module.BillModule;
import com.heji.server.result.Result;
import com.heji.server.service.BillBackupServer;
import com.heji.server.service.BillService;
import com.heji.server.service.ImageService;
import com.heji.server.utils.TimeUtils;
import io.jsonwebtoken.JwtParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    //备份(目前是删除的做备份)
    final
    BillBackupServer billBackupServer;
    //账单照片存储
    final ImageService imageService;

    public BillsController(BillService billService, StorageService storageService, ImageService imageService, BillBackupServer billBackupServer) {
        this.billService = billService;
        this.storageService = storageService;
        this.imageService = imageService;
        this.billBackupServer = billBackupServer;
    }

    @ResponseBody
    @PostMapping(value = {"/add"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addBill(@RequestBody BillModule billModule) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        MBill mBill = new MBill(billModule);
        mBill.setCreateUser(username);
        String billID = billService.addBill(mBill);
        return Result.success(billID);
    }

    @ResponseBody
    @GetMapping(value = {"/info"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBillInfo(@RequestParam String billId) {
        MBill bills = billService.getBillInfo(billId);
        return Result.success(bills);
    }


    @ResponseBody
    @PostMapping(value = {"/getBills"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBills(@RequestParam long startTime, @RequestParam long endTime) {
        //TODO 根据时间段查账单
        List<MBill> bills = billService.getBills(startTime, startTime);
        return Result.success(bills);
    }

    @ResponseBody
    @GetMapping(value = {"delete"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteById(@RequestParam String _id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        MBill mBill = billService.getBillInfo(_id);

        if (null != mBill) {
            if (!mBill.getCreateUser().equals(username)) {//查询是否是该用户创建
                return Result.error("非法跨权限删除操作");
            }
        } else {
            log.info("该账单已经不存在 id{}", _id);
        }
        //imageService.markAsDelete(_id);//不用标记为已删除,删除表以后无法通过ID找到该图片,可以在备份表中更具ID删除这些图片
        //imageService.removeBillImages(_id);//删除照片

        boolean isDeleted = billService.removeBill(_id);//删除账单
        if (isDeleted) {//删除成功,写入备份表(回收站)
            MBillBackup backupBill=  new MBillBackup(mBill);
            billBackupServer.backup(backupBill);
        }
        return Result.success("删除成功:", _id);
        //return Result.success(_id);
    }

    @ResponseBody
    @PostMapping(value = {"update"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String updateBill(@RequestBody MBill bill) {
        billService.updateBill(bill);
        return Result.success("更新成功", bill.get_id());
    }

    @PostMapping("/export")
    @ResponseBody
    public ResponseEntity<Resource> exportBills(@RequestParam(defaultValue = "0") long startDateTime, @RequestParam(defaultValue = "0") long endDateTime) {
        List<MBill> bills = billService.getBills(startDateTime, endDateTime);
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
                    //excel.setTime(TimeUtils.millis2String(bill.getTime(), "yyyy/MM/dd HH:mm:ss"));
                    excel.setTime(bill.getTime());
                    excel.setMoney(String.valueOf(bill.getMoney()));
                    excel.setRemark(bill.getRemark());
                    excel.setType(bill.getType() == 1 ? "收入" : "支出");
                    excel.setCategory(bill.getCategory());
                    return excel;
                }).collect(Collectors.toList());
    }

}
