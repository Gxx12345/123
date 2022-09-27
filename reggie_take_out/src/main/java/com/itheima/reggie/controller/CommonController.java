package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 *图片上传、下载
 * @author my
 * @since 2022/9/27 10:20
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String pasePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //获取上传文件名及后缀名
        String oldName = file.getOriginalFilename();

        String suffix = oldName.substring(oldName.lastIndexOf("."));

        //使用UUID生成新的文件名
        String uuid = UUID.randomUUID().toString();
        //重新生成后的文件名
        String newFileName = uuid+suffix;
        //创建图片保存的基本目录
        File dirs = new File(pasePath);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        //保存临时文件到图片目录下
        try {
            file.transferTo(new File(pasePath,newFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回新图片名称
        return R.success(newFileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //定义输入流，通过输入流读取文件内容
            File file = new File(pasePath, name);
            FileInputStream fileInputStream = new FileInputStream(file);
            //通过response对象设置响应数据格式(image/jpeg)
            response.setContentType("image/jpeg");
            //通过response对象获取到输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //通过输入流读取文件数据，通过输出流写回浏览器
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
            }
            //关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
