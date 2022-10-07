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


@Slf4j
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求路径
        String requestURI = request.getRequestURI();

        //筛选不需要过滤的路径
        String[] uris = new String[]{
                "/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };


        //对比传进来的路径和需要过滤的路径是否匹配
        for (String uri : uris) {
            boolean match = ANT_PATH_MATCHER.match(uri, requestURI);

            if (match) {
                //匹配 ， 直接放行
                filterChain.doFilter(request, response);
                return;
            }
        }

        //获取到当前登录对象ID
        Long ID = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        //判断Session对象是否为null
        if (ID != null) {
            // 获取到当前登陆对象ID并传入ThreadLocal包装类中以便使用
            BaseContext.setCurrentUserId(ID);
            //不为null直接放行
            filterChain.doFilter(request, response);
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
            filterChain.doFilter(request, response);
            return;
        }



        // 如果没取到,代表未登录
        // 5、如果未登录则返回未登录结果
        //Session为null，返回前端数据，该用户未登录
        String notLogin = JSON.toJSONString(R.error("NOTLOGIN"));
        response.getWriter().write(notLogin);

    }


}
