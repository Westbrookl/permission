package com.jhc.service;

import com.google.common.base.Preconditions;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysRoleMapper;
import com.jhc.exception.ParamException;
import com.jhc.model.SysRole;
import com.jhc.param.RoleParam;
import com.jhc.util.BeanValidator;
import com.jhc.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author jhc on 2019/1/24
 */
@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    public void save(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getId(),param.getName())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole role = SysRole.builder().name(param.getName()).status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        sysRoleMapper.insertSelective(role);
    }

    public void update(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getId(),param.getName())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before);
        SysRole after = SysRole.builder().id(param.getId()).name(param.getName()).status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysRoleMapper.updateByPrimaryKey(after);
    }

    public boolean checkExist(Integer id,String name){
        return sysRoleMapper.getByIdAndName(id,name) > 0;
    }

    public List<SysRole> getAll(){
        return sysRoleMapper.getAllRole();
    }
}
