package com.itheima.ruji.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.lang.model.element.VariableElement;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    private static  final  AntPathMatcher ANT_PATH_MATCHER=new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("进入过滤器");
        HttpServletRequest httpServletRequest= (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse= (HttpServletResponse) servletResponse;
        //获取本次请求的URI
        String currentUri = httpServletRequest.getRequestURI();
        //带服务器地址的路径 URl  取得是是绝对路径 (整个路径)
       // StringBuffer requestURL = httpServletRequest.getRequestURL();
        //定义不需要效验的请求地址
      String []uris =new String[]{
           "/backend/**",
          "/front/**",
          "/employee/login",
          "/employee/logout"
  };
        for (String uri : uris) {
            boolean match = ANT_PATH_MATCHER.match(uri, currentUri);
            if(match){
                log.info("本次放行地址:{}" ,currentUri);
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
            //使用字符串拼接无法满足需求,需要 用路径统配来匹配路径
        }
        //判断登录状态,如果一登录,则直接放行
        //到session 中取值
        //用户登录id
        Long employee = (Long) httpServletRequest.getSession().getAttribute(AntPathmathcherSS.EN_V587);
        //用户id 不能为空
        //到session中取值,如果取到了,就代表用户已经登录了
        if(employee!=null){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //如果未登录则返回未登录结构
        String string = JSON.toJSONString(R.error("NOTLOGIN"));
        httpServletResponse.getWriter().write(string);
    }
}
