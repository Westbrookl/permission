package com.jhc.controller;

import com.jhc.beans.PageQuery;
import com.jhc.beans.PageResult;
import com.jhc.common.JsonData;
import com.jhc.model.SysUser;
import com.jhc.param.UserParam;
import com.jhc.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author jhc on 2019/1/19
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

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
}
