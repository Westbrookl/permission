package com.jhc.filter;

import com.jhc.common.RequestHolder;
import com.jhc.model.SysUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jhc on 2019/1/12
 */
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        SysUser user = (SysUser) request.getSession().getAttribute("user");

        if(user == null ){
            String path = "/signin.jsp";
            response.sendRedirect(path);
            return;
        }
        RequestHolder.add(user);
        RequestHolder.add(request);
        filterChain.doFilter(servletRequest,servletResponse);
        return;

    }

    @Override
    public void destroy() {

    }
}
