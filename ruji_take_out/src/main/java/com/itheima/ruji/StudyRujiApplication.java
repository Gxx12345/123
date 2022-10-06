package com.itheima.ruji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionalEventListener;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableAsync
@EnableCaching
public class StudyRujiApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyRujiApplication.class,args);
        System.out.println("................................\n" +
                ".▄▄▄▄▄...........▄..............\n" +
                "█▀▀▀▀██▄.........█▄.......▄.....\n" +
                "█.▄...██.........███▄..▄▄██.....\n" +
                "▀█▀...███......▄████████████....\n" +
                "......██▀......████▄████▄███▄...\n" +
                ".....██▀.......▀████▀▀███████...\n" +
                "....██▀..▄▄▄▄....▀███▄▄▄▄██▀....\n" +
                "....██▄▄██████▄...█████▀▀▀......\n" +
                ".....▀██████████▄████▀..........\n" +
                ".......█████████████............\n" +
                "....▄▄█████████████████▄▄.......\n" +
                "..███▀...██▀▀▀▀████...▀███▄.....\n" +
                ".........██.....▀██▄....▀▀██....\n" +
                ".........▀▀.......▀▀............\n" +
                "................................\n" +
                "Reggie 启动成功");
    }
}

