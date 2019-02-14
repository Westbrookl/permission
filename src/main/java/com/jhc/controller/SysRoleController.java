package com.jhc.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jhc.common.JsonData;
import com.jhc.model.SysRoleUser;
import com.jhc.model.SysUser;
import com.jhc.param.RoleParam;
import com.jhc.service.*;
import com.jhc.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jhc on 2019/1/24
 */
@Controller
@RequestMapping(value = "/sys/role")
public class SysRoleController {
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysTreeService sysTreeService;

    @Resource
    private SysRoleAclService sysRoleAclService;
    @Resource
    private SysRoleUserService sysRoleUserService;
    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/role.page")
    public ModelAndView page(){
        return new ModelAndView("role");
    }
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(RoleParam param){
        sysRoleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(RoleParam param){
        sysRoleService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData list(){
        return JsonData.success(sysRoleService.getAll());
    }

    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roleTree(@RequestParam("roleId")int roleId){
        return JsonData.success(sysTreeService.roleTree(roleId));
    }

    /**
     * 这个方法的作用是改变当前角色的权限的值
     * @param roleId
     * @param aclIds
     * @return
     */
    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(@RequestParam("roleId") int roleId,@RequestParam(value="aclIds",required = false,defaultValue = "")String aclIds){
        List<Integer> aclIdList = StringUtil.splitToListInt(aclIds);
        sysRoleAclService.changeRoleAcls(roleId,aclIdList);
        return JsonData.success();
    }

    /**
     * 得到当前角色下面所有的用户以及当前角色下面已经有了的用户
     *
     * @param roleId
     * @return
     */
    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData usersList(@RequestParam("roleId") int roleId){
        //得到当前角色下的所有的用户
        List<SysUser> roleUser = sysRoleUserService.getUserByRoleId(roleId);
        //得到所有的用户
        List<SysUser> allUsers = sysUserService.getAll();
        List<SysUser> unselectedUser = Lists.newArrayList();
        //计算出所有剩余的用户
        Set<Integer> roleUserId = roleUser.stream().map(sysUser -> sysUser.getId()).collect(Collectors.toSet());
        for(SysUser user : allUsers) {
            if (user.getStatus() == 1 && !roleUserId.contains(user.getId())) {
                unselectedUser.add(user);
            }
        }
        Map<String,List<SysUser>> map = Maps.newHashMap();
        map.put("selected",roleUser);
        map.put("unselected",unselectedUser);
        return JsonData.success(map);
    }

    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData changeUsers(@RequestParam("roleId")int roleId,@RequestParam(value = "userIds",required = false)String userIds){
        List<Integer> userIdList = StringUtil.splitToListInt(userIds);
        sysRoleUserService.changeRoleUsers(roleId,userIdList);
        return JsonData.success();
    }
}
