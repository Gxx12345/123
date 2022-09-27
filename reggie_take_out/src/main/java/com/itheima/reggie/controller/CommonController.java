package com.itheima.reggie.controller;

import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传下载
 *
 * @author yjiiie6
 * @since 2022/9/27 10:19
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
            throw new CustomException("不能上传空文件");
        }
        // 获取文件名和后缀名
        // 获取文件名
        String oldFileName = file.getOriginalFilename();
        // 在这个文件名中拿到后缀
        // 123.txt
        // 拿到的是文件名的后缀
        String suffix = oldFileName.substring(oldFileName.lastIndexOf("."));
        // 使用UUID重新生成图片文件名.
        String uuid = UUID.randomUUID().toString();
        // 新生成的文件名
        String newFileName = uuid + suffix;
        // 图片保存的基本目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            // 创建文件对象
            File newFile = new File(basePath, newFileName);
            // 转移文件.
            file.transferTo(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(newFileName);
    }


    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        // 1). 定义输入流，通过输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath, name));
            // 2). 通过response对象设置响应数据格式(image/jpeg)
            response.setContentType("image/jpeg");
            // 3). 通过response对象，获取到输出流
            ServletOutputStream outputStream = response.getOutputStream();
            // 4). 通过输入流读取文件数据，然后通过输出流写回浏览器
            int len;
            byte[] bytes = new byte[1024];
            while ((len = (fileInputStream.read(bytes))) != -1) {
                // 写回到浏览器中
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            // 5). 关闭资源(仅关闭本地文件流即可)
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
