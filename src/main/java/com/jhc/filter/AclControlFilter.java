package com.jhc.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.jhc.common.ApplicationContextHelper;
import com.jhc.common.JsonData;
import com.jhc.common.RequestHolder;
import com.jhc.model.SysUser;
import com.jhc.service.SysCoreService;
import com.jhc.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jhc on 2019/2/15
 */
@Slf4j
public class AclControlFilter implements Filter {
    private static Set<String> exclusionSet = Sets.newConcurrentHashSet();
    private final static String noAuthUrl = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclusionUrl = filterConfig.getInitParameter("exclusionUrls");
        List<String> exclusionUrlList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(exclusionUrl);
        exclusionSet = Sets.newConcurrentHashSet(exclusionUrlList);
        exclusionSet.add(noAuthUrl);

    }

    /**
     * 设计的逻辑
     * 如果路径在白名单里面则直接跳转
     * 如果当前用户没有登录，则对其进行拦截，并将其跳转
     * 如果当前用户没有访问的权限，也对其进行拦截进行跳转
     * 否则的话正常的进行
     *
     * 其中对其中的ServletPath进行判断，如果以.json结尾的话，返回的是Json串
     * 如果是.page进行结尾的话返回的是一个页面。
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();
        Map requestMap = request.getParameterMap();
        if (exclusionSet.contains(servletPath)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        SysUser sysUser = RequestHolder.getCurrentUser();
        if (sysUser == null) {
            log.info("Someone visit {},but no auth to login,parameter:{}", servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request, response);
            return;
        }
        SysCoreService sysCoreService = ApplicationContextHelper.popBean(SysCoreService.class);
        if (!sysCoreService.hasAclUrl(servletPath)) {
            log.info("{} visit {},but no login,parameter:{}", JsonMapper.obj2String(sysUser), servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request, response);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String servletPath = request.getServletPath();
        if (servletPath.endsWith(".json")) {
            JsonData data = JsonData.fail("没有权限访问，如需访问请联系管理员");
            response.setHeader("Content-Type", "application/json");
            response.getWriter().print(JsonMapper.obj2String(data));
        } else {
            clientRedirect(noAuthUrl, response);
            return;
        }
    }

    private void clientRedirect(String url, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html");
        response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                + "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
    }

    @Override
    public void destroy() {

    }
}
