package com.jhc.controller;

import com.jhc.beans.PageQuery;
import com.jhc.common.JsonData;
import com.jhc.param.SysAclParam;
import com.jhc.service.SysAclService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author jhc on 2019/1/24
 */
@Controller
@RequestMapping("/sys/acl")
public class SysAclController {
    @Resource
    private SysAclService sysAclService;

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
}
