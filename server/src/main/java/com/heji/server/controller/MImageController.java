package com.heji.server.controller;

import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mysql.Bill;
import com.heji.server.data.mongo.MBillImage;
import com.heji.server.exception.NotFindBillException;
import com.heji.server.module.BillModule;
import com.heji.server.result.Result;
import com.heji.server.service.BillService;
import com.heji.server.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/image")
@Slf4j
public class MImageController {

    final ImageService imageService;
    final BillService billService;

    @Autowired
    public MImageController(ImageService imageService, BillService billService) {
        this.imageService = imageService;
        this.billService = billService;
    }

    /**
     * @param imageId 图片ID
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/{imageId:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage(@PathVariable String imageId) throws IOException {
        MBillImage img = imageService.getImage(imageId);
        if (Objects.isNull(img))
            throw new NotFindBillException("账单图片没找到");
        byte[] imgBytes = img.getData();
        return imgBytes;
    }


    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("file") MultipartFile file, @RequestParam(name = "billID", defaultValue = "0") String billID) throws IOException {
        if (Objects.isNull(billID) && billID.equals(""))
            return Result.error("账单不存在");
        MBill bill = billService.getBillInfo(billID);
        if (Objects.isNull(bill))
            throw new NotFindBillException("账单不存在");
        MBillImage image = new MBillImage();
        image.setFilename(file.getOriginalFilename());
        image.setExt(".jpg");
        image.setData(file.getBytes());
        image.setLength(file.getSize());
        image.setBillId(billID);
        String imgId = imageService.saveImage(image);
        String[] imageArray = bill.getImages();
        if (null != imageArray && imageArray.length > 0) {
            String[] newImageArray = Arrays.copyOf(imageArray, imageArray.length + 1);
            newImageArray[newImageArray.length - 1] = imgId;
            bill.setImages(newImageArray);
        } else {
            imageArray = new String[]{imgId};
            bill.setImages(imageArray);
        }
        billService.updateBill(bill);

        log.info("上传文件 OriginalFilename={}, SaveFileName={}", file.getOriginalFilename(), imgId);
        return Result.success(imgId);
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
        billService.addBill(new BillModule());
    }

}
