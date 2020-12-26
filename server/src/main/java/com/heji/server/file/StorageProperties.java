package com.heji.server.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
@Component
public class StorageProperties {

    private String locations = "/storage/images";
    private String excel = "/storage/excel";

    public String getLocation() {
        return createDirIfNotExists();
    }

    public void setLocation(String location) {
        this.locations = location;
    }

    /**
     * 创建文件夹路径
     */
    private String createDirIfNotExists() {
        //获取跟目录
        //第五种
        ApplicationHome h = new ApplicationHome(getClass());
        File file = h.getSource();
        log.info("Root dir={}", file.getAbsoluteFile());
//        try {
//            //file = new File(ResourceUtils.getURL("classpath:").getPath());
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("获取根目录失败，无法创建上传目录！");
//        }
        if (!file.exists()) {
            file = new File("");
        }

        File uploadImage = new File(file.getParent() + locations);
        if (!uploadImage.exists()) {
            uploadImage.mkdirs();
        }
        log.info("Image file dir={}", uploadImage.getAbsoluteFile());
        return uploadImage.getAbsolutePath();
    }
}