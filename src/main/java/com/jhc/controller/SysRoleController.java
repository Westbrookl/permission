package com.jhc.controller;

import com.jhc.common.JsonData;
import com.jhc.param.RoleParam;
import com.jhc.service.SysRoleAclService;
import com.jhc.service.SysRoleService;
import com.jhc.service.SysTreeService;
import com.jhc.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

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

}
