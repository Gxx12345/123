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
 * 过滤器
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                log.info("本次请求{}不需要处理",requestURI);
                filterChain.doFilter(request,response);
                return;
            }
        }
        //4、判断登录状态，如果已登录，则直接放行
        Long employeeId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        if(employeeId != null){
            //如果当前用户已登录，那么需要把当前用户登录的ID，放入到Threadlocal中，以便在后续的线程调用中使用相关的值
            BaseContext.setCurrentId(employeeId);

            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY));
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
}
