package com.jhc.common;

import com.jhc.model.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jhc on 2019/1/21
 * 这个类的主要的作用是为了存储全局的变量的 user 和 request两个变量并且不同的线程对应于不同的值
 *
 * 要注意对变量的清除否则会导致内存泄漏
 *
 * 对于一个Interceptor来说 这属于面向切面的编程在请求处理前和处理请求完成后 针对于http请求来说就是logout
 */
public class RequestHolder {
    private  final static ThreadLocal<SysUser>   userHolder = new ThreadLocal<>();

    private final static ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void add(SysUser user){
        userHolder.set(user);
    }
    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser(){
        return userHolder.get();
    }
    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }

    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }
}
