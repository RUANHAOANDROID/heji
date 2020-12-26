package com.heji.server.data.controller;

import com.heji.server.data.Bill;
import com.heji.server.data.repository.BillDao;
import com.heji.server.file.StorageService;
import com.heji.server.result.Response;
import com.heji.server.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/file")
@Slf4j
public class ImageController {
    BillDao billDao;
    private final StorageService storageService;

    @Autowired
    public ImageController(StorageService storageService, BillDao billDao) {
        this.storageService = storageService;
        this.billDao = billDao;
    }

    @GetMapping("/downloadFile/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    /**
     *
     * @param filename 图片ID
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/image/{filename:.+}",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] images(@PathVariable String filename) throws IOException {
        Resource file = storageService.loadAsResource(filename);
        return IOUtils.toByteArray(file.getInputStream());
    }


    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(name = "billID", defaultValue = "0") String billID) {
        if (Objects.isNull(billID) && billID.equals(""))
            return Result.error("账单不存在");
        Bill bill = billDao.findBillByUid(billID);
        if (Objects.isNull(bill)) {
            Response dataIsNull = Response.DATA_IS_NULL;
            return Result.error(dataIsNull);
        }
        String fileId = UUID.randomUUID().toString();
        storageService.store(file, fileId);
        saveTicketInfo(bill, fileId);
        log.info("上传文件 OriginalFilename={}, SaveFileName={}", file.getOriginalFilename(), fileId);
        return Result.success(fileId);
    }


    @PostMapping("/uploadFiles")
    public String uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        List<String> tickets = new ArrayList<>();
//        files.forEach(file -> {
//            String fileName = UUID.randomUUID().toString();
//            storageService.store(file, fileName);//存储文件
//            String billID = file.getOriginalFilename();
//            String ticketImg = saveTicketInfo(billID, fileName, System.currentTimeMillis());
//            tickets.add(ticketImg);
//        });
        String result = Result.success(tickets);
//        log.debug(result);
        return result;
    }

    /**
     * 图片信息入库(bill表)
     *
     * @return
     */
    private void saveTicketInfo(Bill bill, String fileId) {
        log.debug("save {}", bill.toString());
        List<String> images = bill.getImages();
        if (null == images) {
            images = new LinkedList<>();
        }
        images.add(fileId);
        bill.setImages(images);
        billDao.save(bill);
    }

}
