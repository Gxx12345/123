package com.alibaba.reggie.filter;

import com.alibaba.fastjson.JSON;
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

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        log.info("拦截到请求"+requestURI);
        String[] uris = {"/backend/**", "/front/**", "/employee/login", "/employee/logout"};
        for (String uri : uris) {
            AntPathMatcher matcher = new AntPathMatcher();
            if (matcher.match(uri,requestURI)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        if (null != employeeId) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String notlogin = JSON.toJSONString(Result.error("NOTLOGIN"));
        response.getWriter().write(notlogin);
    }
}
