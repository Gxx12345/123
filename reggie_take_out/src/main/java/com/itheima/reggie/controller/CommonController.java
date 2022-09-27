package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用控制器
 * 2).在controller包创建CommonController
 *
 * @author Gmy
 * @since 2022/9/27 10:22
 */
@Slf4j
@RestController
@RequestMapping("common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("前后端联通");
        //  3).获取文件名和后缀名
        //  上传的名称
        //  abc.txt
        String oldName = file.getOriginalFilename();
        //  .txt
        //  文件名的后缀
        String suffix = oldName.substring(oldName.indexOf("."));
        //  4).使用UUID重新生成新图片文件名
        UUID uuid = UUID.randomUUID();
        //  重新弄生吃成后的文件名
        String newFileName = uuid + suffix;
        //  5).创建图片保存的基本目录
        //  用这个路径新建了一个file对象
        //  面试题: new file 会创建文件吗
        File dirs = new File(newFileName);
        //  判断路径是否存在dire.exists
        //  判断路径不存在，取反，取反，取反
        if (!dirs.exists()) {
            //  如果路径不存在，就新建此位置
            dirs.mkdirs();
        }
        //  6).保存临时文件到图片目录下
        try {
            //  临时文件会转移到图片目录
            file.transferTo(new File(basePath, newFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  7).返回新图片文件名
        return R.success(newFileName);
    }

    @GetMapping("download")
    public void download(String name, HttpServletResponse response) {
        try {
            //  1.定义输入流，通过输入流读取文件内容
            File file = new File(basePath, name);
            FileInputStream fileInputStream = new FileInputStream(file);
            //  2.通过response对象设置响应数据格式("image/jpeg")
            response.setContentType("image/jpeg");
            //  3.通过response对象，获取到输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //  4.通过输入流读取文件数据，然后通过输出流写回浏览器
            int len = 0;
            //  每次往浏览器中写的1kb的数据
            byte[] bytes =new byte[1024];
            //  这行代码是会返回每次读取的数组的长度
            //  当文件读完之后，会返回一个-1
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //  5.关闭资源(仅关闭本地文件流即可)
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
