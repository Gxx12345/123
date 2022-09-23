package com.itheima.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * mvc相关的配置类
 *
 * @author t3rik
 * @since 2022/9/23 14:57
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
        /**
         * 设置静态资源映射
         * @param registry
         */
        @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        log.info("静态资源映射成功");
    }
}