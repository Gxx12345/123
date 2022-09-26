package com.alibaba.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.reggie.common.BaseContext;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
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
    //定义不可更改的AntPathMatcher路径匹配器对象
    public static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //将请求和响应强制类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //请求获取URI信息
        String requestURI = request.getRequestURI();
        log.info("拦截到请求"+requestURI);
        //定义不用拦截的路径,进行判断
        String[] uris = {
                "/backend/**", "/front/**",
                "/employee/login", "/employee/logout"};
        for (String uri : uris) {
            //AntPathMatcher路径匹配器对象的match方法比较路径
            if (MATCHER.match(uri,requestURI)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        //根据session值取得employeeId,判断id是否为null
        Long employeeId = (Long) request.getSession().getAttribute(GlobalConstant.EMPLOYEE_KEY);
        //存放当前登录用户到ThreadLocal
        BaseContext.setSetThreadLocalCurrentId(employeeId);
        if (null != employeeId) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //session的employeeID为null时,响应NOTLOGIN信息
        String notlogin = JSON.toJSONString(Result.error("NOTLOGIN"));
        response.getWriter().write(notlogin);
    }
}
