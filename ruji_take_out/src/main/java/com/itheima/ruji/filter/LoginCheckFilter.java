package com.itheima.ruji.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.BaseTreadlock;
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
        //获取本次请求的URI 是相对路径
        String currentUri = httpServletRequest.getRequestURI();
        //带服务器地址的路径 URl  取得是是绝对路径 (整个路径)
       // StringBuffer requestURL = httpServletRequest.getRequestURL();
        //定义不需要效验的请求地址
      String []uris =new String[]{
           "/backend/**",
          "/front/**",
          "/employee/login",
          "/employee/logout","/common/**"
  };
        for (String uri : uris) {
            boolean match = ANT_PATH_MATCHER.match(uri, currentUri); //前面是我们要匹配的内容,后面是前端传来的内容
            if(match){
                log.info("本次放行地址:{}" ,currentUri);
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
            //使用字符串拼接无法满足需求,需要 用路径统配来匹配路径

            //传统字符串拼接会有一定问题,在静态页面会有一定程度的匹配不上,所以用路径匹配
            //index.html是页面,这个就匹配不上,所以就不会放行
          /*  for (String s : uris) {
                if(currentUri.equals(s)){
                    filterChain.doFilter(servletRequest,servletResponse);
                    return;
                }
            }
            */
        }
        //判断登录状态,如果一登录,则直接放行
        //到session 中取值
        //用户登录id
        Long employee = (Long) httpServletRequest.getSession().getAttribute(AntPathmathcherSS.EN_V587);
        //用户id 不能为空
        //到session中取值,如果取到了,就代表用户已经登录了
        if(employee!=null){
            log.info("过滤器当前线程的id:{}",Thread.currentThread().getName());
            //如果当前用户已登录,那么需要把当前用户登录的id,放入到Threadlocal中, 以便后续的线程调用中使用相关的值
            BaseTreadlock.setCurrentId(employee);

            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //如果未登录则返回未登录结构
        String string = JSON.toJSONString(R.error("NOTLOGIN"));
        httpServletResponse.getWriter().write(string);



     /*   扩展index.html是页面,应为上面定义的内容已经是放行状态了,但是一访问这个还会
        跳转到登录页面?
        答: 应为一访问index.html,其实内部已经放行了.但是在这个页面以后会第一时间要跳转到/employee/page进行
        分页查询,给用户展现数据.  但是/employee/page没有被放行.所以跳回到了登录页面

*/



    }
}
