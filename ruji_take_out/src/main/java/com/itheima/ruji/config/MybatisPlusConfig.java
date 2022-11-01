package com.itheima.ruji.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分页插件
 *
 * @author Gzz
 * @since 2022/9/24 17:05
 */
//没插分页插件的时候只是代码中只是做了一遍查询
//插入这个分页插件,mybatis-plus 会进行俩次查询,第一次查询查的是符合查询条件的总条数.
// 完了第二次再通过条件来查询的结构还有第一次查询的条数进行分页limit
@Configuration
public class MybatisPlusConfig {
   // 分页插件相当于mybatis-pus加了一个拦截器
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        //mp 的拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        //要在拦截器中添加分页插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
