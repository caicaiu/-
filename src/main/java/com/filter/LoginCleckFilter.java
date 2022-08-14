package com.filter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.BaseContext;
import com.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截器
 *
 * @author 才
 */
@WebFilter(filterName = "logincheckfilter",urlPatterns = "*")
@Slf4j
public class LoginCleckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //1. 获取请求路径
        String requestURI = request.getRequestURI();

        //放行的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/common/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        //如果不需要处理，则直接放行
        boolean  check = check(requestURI,urls);
        //为true则不需要过滤
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        //4、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee")!=null) {
            Long id = (Long)request.getSession().getAttribute("employee");

            BaseContext.setId(id);
            log.info("过滤器线程id为：{}",id);
            filterChain.doFilter(request,response);
            return;
        }

        //4、判断手机登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user")!=null) {
            Long id = (Long)request.getSession().getAttribute("user");

            BaseContext.setId(id);
            log.info("过滤器线程id为：{}",id);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录
        // 则返回未登录结果,响应回去的结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 匹配通配符，判断请求路径是否放行路径的请求
     * @param requestURL
     * @param urls
     * @return
     */
    public boolean check(String requestURL , String[] urls){
        //依次遍历，检查请求路径是否包含过滤的路径
        for(String url:urls){
            if( PATH_MATCHER.match(url, requestURL)){
                return true;
            }
        }
        return false;
    }

}
