package com.heji.server.controller;

import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mysql.Bill;
import com.heji.server.data.mongo.MBillImage;
import com.heji.server.exception.NotFindBillException;
import com.heji.server.exception.NullFileException;
import com.heji.server.module.BillModule;
import com.heji.server.result.Result;
import com.heji.server.service.BillService;
import com.heji.server.service.ImageService;
import com.heji.server.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.security.provider.MD5;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping(path = "/image")
@Slf4j
public class ImageController {

    final ImageService imageService;
    final BillService billService;

    @Autowired
    public ImageController(ImageService imageService, BillService billService) {
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
    public String uploadImage(@RequestParam("image") MultipartFile imageFile, @RequestParam(name = "billId", defaultValue = "0") String billID) throws IOException, NoSuchAlgorithmException {
        if (Objects.isNull(billID) && billID.equals(""))
            return Result.error("账单不存在");
        MBill bill = billService.getBillInfo(billID);
        if (Objects.isNull(bill))
            throw new NotFindBillException("账单不存在");
        String md5 = MD5Util.getMD5(imageFile.getInputStream());
        MBillImage image = new MBillImage();
        image.setFilename(imageFile.getOriginalFilename());
        image.setExt(".jpg");
        image.setData(imageFile.getBytes());
        image.setLength(imageFile.getSize());
        image.setBillId(billID);
        image.setMd5(md5);
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

        log.info("上传文件 OriginalFilename={}, SaveFileName={}", imageFile.getOriginalFilename(), imgId);
        return Result.success(imgId);
    }


    @PostMapping("/uploadImages")
    public String uploadFiles(@RequestParam("images") List<MultipartFile> images, @RequestParam("billId") String billId) throws IOException, NoSuchAlgorithmException {

        Objects.requireNonNull(images);
        if (images.size() <= 0)
            throw new NullFileException("上传文件为空");
        if (!billService.exists(billId))
            throw new NotFindBillException("账单不存在");
        List<String> imageIds = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            MultipartFile imageFile = images.get(i);
            String md5 = MD5Util.getMD5(imageFile.getInputStream());
            //获取文件后缀名
            String extName = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
            //防止文件重复 获取当前时间给文件重命名
            ObjectId objectId = new ObjectId();
            String newFileName = objectId.toString() + extName;
            //重命名
            MBillImage image = new MBillImage().set_id(objectId)
                    .setFilename(newFileName)
                    .setExt(extName)
                    .setData(imageFile.getBytes())
                    .setLength(imageFile.getSize())
                    .setBillId(billId)
                    .setMd5(md5);
            imageService.saveImage(image);
            imageIds.add(objectId.toString());
        }
        //更新账单照片
        billService.upInstImages(billId, imageIds.toArray(new String[imageIds.size()]));
        return Result.success(imageIds);
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
