package com.itheima.ruji.controller;

import com.itheima.ruji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * 文件上传
 *
 * @author Gzz
 * @since 2022/9/27 10:25
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommomColltroller {
    //在配置文件中取值
    //第一种:@value
    //第二种:用注解@ConfigurationProperties(perfix=)
    //的第三种 Environment
    //YML文件配置上传路径
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //获取文件名和后缀名
        String oldName = file.getOriginalFilename();
        //获取文件后缀
        String substring = oldName.substring(oldName.lastIndexOf("."));
        //使用UUID重新获取新图片的名字
        String uuidString = UUID.randomUUID().toString();
        //重新获取新图片名字
        String newFilename = uuidString + substring;
        //创建图片保存的基本目录
        //用这个路径(E:\images)新建了一个File对象
        //只是在内存中建立一个File对象
        //面试题:new file会创建文件吗?  不会,只是在内存中创建了一个File对象
        File dirs = new File(basePath);
        //判断路径是否存在dirs.exists
        //判断路径不存在取反,
        if (!dirs.exists()) {
            //如果路径不存在,新建此路径
            dirs.mkdirs();
        }
        //保存临时文件到图片目录下
        try {
            //临时文件会转移到图片目录
            File newFile = new File(basePath, newFilename);
            file.transferTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("前后端联通");
        return R.success(newFilename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //定义输入流,通过输入流读取文件
            File file = new File(basePath, name);
            FileInputStream fileInputStream = new FileInputStream(file);
            //通过response对象设置响应数据格式:(image/jpeg)
            response.setContentType("image/jpeg");
            //第二种 response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            fileInputStream.close();
        } catch (Exception e) {
           throw  new RuntimeException(e);
        }
    }
}

