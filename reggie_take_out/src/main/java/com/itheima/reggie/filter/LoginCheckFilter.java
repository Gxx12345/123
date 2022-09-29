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
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求: {}", requestURI);

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**", "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4、判断登录状态，如果已登录，则直接放行
        Long employee = (Long) request.getSession().getAttribute("employee");
        if (employee != null) {
            log.info("用户已登录，用户id为:{}", request.getSession().getAttribute("employee"));
            BaseContext.setCurrentUserId(employee);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
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
        //5、如果未登录则返回登录结果，通过输出流方式想客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
