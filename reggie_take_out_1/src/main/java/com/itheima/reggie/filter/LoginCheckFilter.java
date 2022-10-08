package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器
 *
 * @author t3rik
 * @since 2022/9/24 09:25
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("进入了过滤器");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 这行代码是放行请求
//        filterChain.doFilter(servletRequest, servletResponse);
        // 1、获取本次请求的URI
        // 本地请求地址
        String currentUri = request.getRequestURI();
        // 带服务器地址的路径 url
        // StringBuffer requestURL = request.getRequestURL();
        // 2、判断本次请求是否需要校验用户登录状态
        // 根据请求地址
        // 要先定义我们不需要校验的请求地址
        String[] uris = new String[]{
                "/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout","/common/**",
                "/user/sendMsg","/user/login"
        };

        for (String uri : uris) {
            // 校验放行,就是在做请求地址的字符串匹配
            // 如果一致,那么直接放行
            // 如果不一致,不会放行
            // 如果匹配成功会返回true
            // 匹配失败,返回false
            boolean match = ANT_PATH_MATCHER.match(uri, currentUri);
            // 是否匹配成功
            // 3、如果不需要处理，则直接放行
            if (match) {
                log.info("本次放行的请求地址 ====> {}", currentUri);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            //region 使用字符串匹配无法满足我们的需求
            //            if (currentUri.equals(uri)) {
            //                log.info("本次放行的请求地址 ====> {}", currentUri);
            //                filterChain.doFilter(servletRequest, servletResponse);
            //                return;
            //            }
            //endregion
        }
        // 4、判断登录状态，如果已登录，则直接放行
        // 到session中取值
        // 用户登录ID
        Long employeeId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        // 到session中取值,如果取到了,就代表这个用户已经登录了
        // 用户ID不为空,代表已登录
        if (employeeId != null) {
            // 放行
            // 当前线程ID
            // 如果当前用户已登录,那么需要把当前用户登录的ID,放入到Threadlocal中,以便在后续的线程调用中使用相关的值
            BaseContext.setCurrentUserId(employeeId);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 移动端的判断逻辑
        Long userId = (Long) request.getSession().getAttribute(GlobalConstant.MOBILE_KEY);
        // 到session中取值,如果取到了,就代表这个用户已经登录了
        // 用户ID不为空,代表已登录
        if (userId != null) {
            // 放行
            // 当前线程ID
            // 如果当前用户已登录,那么需要把当前用户登录的ID,放入到Threadlocal中,以便在后续的线程调用中使用相关的值
            BaseContext.setCurrentUserId(userId);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 如果没取到,代表未登录
        // 5、如果未登录则返回未登录结果
        String notlogin = JSON.toJSONString(R.error("NOTLOGIN"));
        response.getWriter().write(notlogin);
    }
}
