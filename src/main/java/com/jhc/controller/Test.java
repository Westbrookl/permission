package com.jhc.controller;

import com.jhc.common.ApplicationContextHelper;
import com.jhc.common.JsonData;
import com.jhc.dao.SysAclMapper;
import com.jhc.dao.SysAclModuleMapper;
import com.jhc.exception.PermissionException;
import com.jhc.model.SysAclModule;
import com.jhc.param.TestVo;
import com.jhc.util.BeanValidator;
import com.jhc.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jhc on 2019/1/14
 */
@Controller
@RequestMapping("/test")
@Slf4j
public class Test {
//   private static Logger log = LoggerFactory.getLogger(Test.class);
    @RequestMapping("/hello.json")
    @ResponseBody
    public String hello(){
       log.info("index");
        throw new PermissionException("test exception");
//        return "index";
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData test(TestVo testVo){
        log.info("validate");
//        System.out.println("11111");
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule sysAclModule = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(sysAclModule));
        BeanValidator.check(testVo);
        return JsonData.success("test");
    }
}
