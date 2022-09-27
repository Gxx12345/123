package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.common.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传下载
 *
 * @author cyberengr
 * @since 2022/9/26 20:09
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
            throw new CustomException("不错上传空文件!");
        }
        String oldFileName = file.getOriginalFilename();
        String suffix = oldFileName.substring(oldFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffix;
        File dirs = new File(basePath);
        if (!dirs.exists()) {
            if (!dirs.mkdirs()) {
                throw new CustomException("新建文件夹失败!");
            }
        }
        try {
            file.transferTo(new File(dirs, newFileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("上传文件失败!");
        }
        return Result.success(newFileName);
    }

    @GetMapping("/download")
    public void download(@RequestParam String name, HttpServletResponse response) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(basePath, name));
            //设置返回的是图片JPEG格式
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = (fileInputStream.read(bytes))) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
}
