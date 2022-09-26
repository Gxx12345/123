package com.itheima.ruji.config;

import com.itheima.ruji.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


// resources 里面默认的是静态static里面的前端信息 .可以直接通过浏览器访问. 抛出static外面的就需要映射了
//   addResourceHandler :这个里面第一个括号是映射指定文件的名字里面的内容. 相当于resources
//addResourceLocations: 这个里面是指定映射resources 里面指定的内容
@Configuration
@Slf4j
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

    /**
     * 扩展mvc框架的消息转换器  //  定义完了转换器还得要配置他, 所以下面就是配置 重写extendMessageConverters方法
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //定义一个转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //上面定义的转换器意思就是配置用JacksonObjectMapper里面序列号的格式
        //完了配置完了转换器,spring还不能用,所以要把他加到spring集合中

        //加入到转换器集合中
        converters.add(0,messageConverter);
    }
}
