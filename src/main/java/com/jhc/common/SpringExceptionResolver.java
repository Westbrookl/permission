package com.jhc.common;

import com.jhc.exception.ParamException;
import com.jhc.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jhc on 2019/1/14
 * 这是用来处理全局异常的一个异常处理器
 * 应用于HandlerExceptionHandler
 * 重写了里面的resolveException
 * 取出其中的url以及判断里面的异常的情况
 * 分为两种： 一个是请求Json数据的格式
 * 一个是请求页面。分别以json和page结尾
 * 如果是json页面的话,处理两种异常一种是预期的异常PermissionException用jsonview这个视图解析器去解析
 * 另一种不属于我们预期的异常然后把这个异常的信息也需要抛出
 *
 * 如果是page页面的话就返回page的页面，只不过不同的地方在于需要返回的是一个页面通过ModelAndView()前面用来选择需要返回的页面的解析器，后面是页面的数据
 *
 * 如果是其他的话也返回会到页面 交由InternalViewResolver来解决
 *
 * 另外还需要把这个异常处理类交由Spring来处理
 *
 */
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    private static Logger log = LoggerFactory.getLogger(SpringExceptionResolver.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
        String url = request.getRequestURL().toString();
        ModelAndView mv;
        String msg = "System Error";
        if(url.endsWith(".json")){
            if(ex instanceof PermissionException || ex instanceof ParamException){
                JsonData jsonData = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("jsonView",jsonData.toMap());
            }else{
                log.error("Unknown  json Exception,url "+url,ex);//组成是String + Object
                JsonData jsonData = JsonData.fail(msg);
                mv = new ModelAndView("jsonView",jsonData.toMap());
            }
        }else if(url.endsWith(".page")){
            log.error("Unknown page exception,url"+url,ex);
            JsonData jsonData = JsonData.fail(msg);
            mv = new ModelAndView("exception",jsonData.toMap());
        }else{
            log.error("unknown Exception,url "+url,ex);
            JsonData jsonData = JsonData.fail(msg);
            mv = new ModelAndView("jsonView",jsonData.toMap());
        }
        return mv;
    }
}
