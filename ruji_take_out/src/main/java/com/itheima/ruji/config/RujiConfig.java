package com.itheima.ruji.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



// resources 里面默认的是静态static里面的前端信息 .可以直接通过浏览器访问. 抛出static外面的就需要映射了
//   addResourceHandler :这个里面第一个括号是映射指定文件的名字里面的内容. 相当于resources
//addResourceLocations: 这个里面是指定映射resources 里面指定的内容
@Configuration
public class RujiConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 设置静态资源映射
         * @param registry
         */
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
