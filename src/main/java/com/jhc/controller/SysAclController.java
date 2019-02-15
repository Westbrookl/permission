package com.jhc.controller;

import com.google.common.collect.Maps;
import com.jhc.beans.PageQuery;
import com.jhc.common.JsonData;
import com.jhc.model.SysRole;
import com.jhc.model.SysRoleUser;
import com.jhc.param.SysAclParam;
import com.jhc.service.SysAclService;
import com.jhc.service.SysRoleAclService;
import com.jhc.service.SysRoleService;
import com.jhc.service.SysRoleUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author jhc on 2019/1/24
 */
@Controller
@RequestMapping("/sys/acl")
public class SysAclController {
    @Resource
    private SysAclService sysAclService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysRoleUserService sysRoleUserService;


    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysAclParam param){
        sysAclService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(SysAclParam param){
        sysAclService.update(param);
        return  JsonData.success();
    }


    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData list(@RequestParam("aclModuleId")Integer aclModuleId, PageQuery pageQuery){
        return JsonData.success(sysAclService.gePageByAclModuleId(aclModuleId,pageQuery));
    }

    /**
     * 这个方法是为了得到 这个权限分配给的角色以及权限分配给的用户。
     * 首先得到具有该权限的角色
     *      在sysRoleService中通过权限的Id来获得所有的
     * 然后得到所有具有角色的用户
     * @param aclId
     * @return
     */
    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData aclInfo(@RequestParam("aclId")int aclId){
       List<SysRole> roleList = sysRoleService.getRoleListByAclId(aclId);
        Map<String,Object>  map = Maps.newHashMap();
        map.put("roles",roleList);
        map.put("users",sysRoleService.getUserListByRoleList(roleList));
        return JsonData.success(map);
    }
}
