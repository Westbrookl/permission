package com.jhc.service;

import com.jhc.beans.PageQuery;
import com.jhc.beans.PageResult;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysUserMapper;
import com.jhc.exception.ParamException;
import com.jhc.model.SysUser;
import com.jhc.param.UserParam;
import com.jhc.util.BeanValidator;
import com.jhc.util.MD5Util;
import com.jhc.util.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author jhc on 2019/1/19
 */
@Controller
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    public void save(UserParam userParam){
        BeanValidator.check(userParam);
        if(checkTelephoneExist(userParam.getTelephone(),userParam.getId())){
            throw new ParamException("电话已经被占用");
        }
        if(checkEmailExist(userParam.getEmail(),userParam.getId())){
            throw new ParamException("邮箱被占用");
        }
        String password = PasswordUtil.randomPassword();
        //TODO
        password = "12345678";
        String encrptPassword = MD5Util.encrypt(password);
        SysUser user = SysUser.builder().username(userParam.getUsername()).password(encrptPassword).deptId(userParam.getDeptId()).email(userParam.getEmail())
                .telephone(userParam.getTelephone()).remark(userParam.getRemark()).status(userParam.getStatus()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp("127.0.0.1");
        user.setOperateTime(new Date());
        //TODO:send Email;
        sysUserMapper.insertSelective(user);


    }

    public void update(UserParam userParam){
        BeanValidator.check(userParam);
        if(checkTelephoneExist(userParam.getTelephone(),userParam.getId())){
            throw new ParamException("电话已经存在");
        }
        if(checkEmailExist(userParam.getEmail(),userParam.getId())){
            throw new ParamException("邮箱已经存在");
        }
        SysUser user = SysUser.builder().id(userParam.getId()).username(userParam.getUsername()).status(userParam.getStatus()).deptId(userParam.getDeptId())
                .remark(userParam.getRemark()).email(userParam.getEmail()).telephone(userParam.getTelephone()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp("127.0.0.1");
        user.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 对于两个查看是否存在的内容来说的话
     * 传入要查看的元素和当前用户的id
     * 因为对于新建的用户来说的话没有id 所以需要进行非null判断
     * 而对于更新的用户来说的话 id存在 即在数据空中id不相同的条件下判断
     * 否则就要在id的条件下判断
     * @param telephone
     * @param id
     * @return
     */

    public boolean checkTelephoneExist(String telephone,Integer id){
        return sysUserMapper.countByTelephone(telephone,id) > 0;
    }
    public boolean checkEmailExist(String email,Integer id){
        return  sysUserMapper.countByEmail(email,id) > 0;
    }

    public SysUser findByKeyWord(String username){
        return sysUserMapper.findByKeyWord(username);
    }

    public PageResult<SysUser> getPageByDeptId(Integer id, PageQuery pageQuery){
        BeanValidator.check(pageQuery);
        int count = sysUserMapper.countByDeptId(id);
        if(count > 0){
            List<SysUser> list = sysUserMapper.getPageByDeptId(id,pageQuery);
            return PageResult.<SysUser>builder().total(count).data(list).build();
        }
        return PageResult.<SysUser>builder().build();
    }
}
