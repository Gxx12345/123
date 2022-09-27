package com.itheima.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.GlobalConstant;
import com.itheima.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("进入了过滤器");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //代码放行
        //filterChain.doFilter(servletRequest,servletResponse);
        //1.获取本次请求的URI
        //本地请求地址
        String currentUri = request.getRequestURI();
        //带服务器地址的路径 url
        //StringBuffer requestURL = request.getRequestURL();
        //2.判断本次请求的是否需要校验用户登录状态
        //根据请求地址
        //要先定义我们不需要校验的请求地址
        String[] uris = new String[]{
                "/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout"
        };
        for (String uri : uris) {
            //校验放行
            //如果一致，那么直接放行
            //如果不一致，就不放行
            //如果匹配成功返回true
            //匹配失败返回false
            boolean match = ANT_PATH_MATCHER.match(uri, currentUri);
            //是否匹配成功
            //3.如果不需要处理，则直接放行
            if (match) {
                log.info("本次放行的请求地址 ====>{}",currentUri);
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
        }
        //4.判断登录状态，如果已经登录，则直接放行
        //到session中取值
        //用户目录ID
        Long employeeId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        //到session中取值，如果取到了，那就代表这个用户已经登录了
        //用户ID不为空，代表已登录
        if (employeeId != null) {
            //放行
            log.info("过滤器中：当前线程ID ==>{}",Thread.currentThread().getId());
            //如果当前用户以登录，那么需要把当前用户登录的ID，放入到ThreadLocal中，以便在后续的线程中使用相关的值
            BaseContext.setCurrentUser(employeeId);
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //如果没取到，没代表未登录
        //5.如果未登录则返回未登录结果
        String notlogin = JSON.toJSONString(R.error("NOTLOGIN"));
        response.getWriter().write(notlogin);
    }
}
