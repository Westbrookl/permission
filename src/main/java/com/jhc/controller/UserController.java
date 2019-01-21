package com.jhc.controller;

import com.jhc.model.SysUser;
import com.jhc.service.SysUserService;
import com.jhc.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jhc on 2019/1/20
 * 我们希望的是signin.jsp可以直接被访问不被spring管理
 * 换路径
 *
 * 然后在我们登录失败的时候可以跳转的话 所以不能够在@Controller前面加上一个/user的mapping
 * 这样会导致无法失败的时候跳转
 */
@Controller
public class UserController {
    @Resource
    private SysUserService sysUserService;

    /**
     * 这个函数是用来判断登录的
     * 传入的参数为用户名（可以是手机号也可以是email）
     * 然后根据用户名取出相应的用户
     * 并且保存当前请求的路径
     * 然后对传入的内容进行判断
     * 判断的内容分为：1用户名是否为空 2 密码是否为空 3 用户是否存在 4 用户密码是否正确 5 用户的状态是否正常
     * 如果全为正常的话 进行页面的跳转这里需要注意的request 和response.sendRedirect()的区别，request.getDispathcerServlet是服务器端的跳转而地址栏不发生改变
     * 而response.sendRedirect()这个方法是客户端的跳转 构造一个新的request
     * 如果上面存在错误的话，需要保存错误信息，并且保存username还有Ret的值 然后跳转到新的页面
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        SysUser user = sysUserService.findByKeyWord(username);

        String errorMsgs = "";
        String ret = request.getParameter("ret");

        if(StringUtils.isBlank(username)){
            errorMsgs = "用户名不能为空";
        }else if(StringUtils.isBlank(password)){
            errorMsgs = "密码不能为空";
        }else if(user == null){
            errorMsgs = "用户不存在";
        }else if(!user.getPassword().equals(MD5Util.encrypt(password))){
            errorMsgs = "用户名或者密码错误";
        }else if(user.getStatus() != 1){
            errorMsgs = "账号被冻结，请联系管理员";
        }else{
            // login success
            request.getSession().setAttribute("user",user);
            if(StringUtils.isNotBlank(ret)){
                response.sendRedirect(ret);
            }else{
                response.sendRedirect("/admin/index.page");

            }
            return;

        }

        request.setAttribute("error",errorMsgs);
        request.setAttribute("username",username);
        if(StringUtils.isNotBlank(ret)){
            request.setAttribute("ret",ret);
        }
        String path = "signin.jsp";
        request.getRequestDispatcher(path).forward(request,response);

    }

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        request.getSession().invalidate();
        String path = "signin.jsp";
        response.sendRedirect(path);
    }

}
