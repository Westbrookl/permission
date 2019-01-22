package com.jhc.controller;

import com.jhc.common.JsonData;
import com.jhc.dto.AclModuleLevelDto;
import com.jhc.param.SysAclModuleParam;
import com.jhc.service.SysAclModuleService;
import com.jhc.service.SysTreeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jhc on 2019/1/22
 */

@Controller
@RequestMapping("/sys/aclModule")
public class SysAclModuleController {
    @Resource
    private  SysAclModuleService sysAclModuleService;
    @Resource
    private SysTreeService sysTreeService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysAclModuleParam param){
        sysAclModuleService.save(param);
        return JsonData.success();

    }

    @RequestMapping("/acl.page")
    public ModelAndView page(){
        ModelAndView view = new ModelAndView("acl");
        return view;
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(SysAclModuleParam param){
        sysAclModuleService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<AclModuleLevelDto> dtoList = sysTreeService.aclTree() ;
        return JsonData.success(dtoList);
    }
}
