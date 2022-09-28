package com.itheima.ruji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionalEventListener;

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class StudyRujiApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyRujiApplication.class,args);
    }
}
