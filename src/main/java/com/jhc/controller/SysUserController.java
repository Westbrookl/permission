package com.jhc.controller;

import com.google.common.collect.Maps;
import com.jhc.beans.PageQuery;
import com.jhc.beans.PageResult;
import com.jhc.common.JsonData;
import com.jhc.model.SysUser;
import com.jhc.param.UserParam;
import com.jhc.service.SysRoleService;
import com.jhc.service.SysTreeService;
import com.jhc.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author jhc on 2019/1/19
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysTreeService sysTreeService;
    @RequestMapping("/save.json")
    public JsonData save(UserParam param){
        sysUserService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    public JsonData update(UserParam userParam){
        sysUserService.update(userParam);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(@RequestParam("deptId") int deptId, PageQuery pageQuery){
        PageResult<SysUser> result = sysUserService.getPageByDeptId(deptId,pageQuery);
        System.out.println(result.getTotal());
        return JsonData.success(result);
    }

    /**
     * 为了获取用户所有的角色
     * 用户所拥有的所有的权限
     *
     * @param userId
     * @return
     */
    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData aclInfo(@RequestParam("userId")int userId){

        Map<String,Object> map = Maps.newHashMap();
        map.put("acls",sysTreeService.userAclTree(userId));
        map.put("roles",sysRoleService.getRoleListByUserId(userId));
        return JsonData.success(map);
    }
}
