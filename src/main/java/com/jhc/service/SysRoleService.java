package com.jhc.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysRoleAclMapper;
import com.jhc.dao.SysRoleMapper;
import com.jhc.dao.SysRoleUserMapper;
import com.jhc.dao.SysUserMapper;
import com.jhc.dto.AclModuleLevelDto;
import com.jhc.exception.ParamException;
import com.jhc.model.SysRole;
import com.jhc.model.SysUser;
import com.jhc.param.RoleParam;
import com.jhc.util.BeanValidator;
import com.jhc.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jhc on 2019/1/24
 */
@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysUserMapper sysUserMapper;
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

    public List<SysRole> getRoleListByAclId(int aclId){
        List<Integer> roleIdList = sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }

    /**
     * 通过角色获取 所有的用户实际上还是通过用户的Id 来获取所有的角色的ID
     * @param roleList
     * @return
     */
    public List<SysUser> getUserListByRoleList(List<SysRole> roleList){
        if(CollectionUtils.isEmpty(roleList)){
            return Lists.newArrayList();
        }
        List<Integer> roleIdList = roleList.stream().map(role->role.getId()).collect(Collectors.toList());
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getUserByIdList(userIdList);
    }

   public List<SysRole> getRoleListByUserId(@Param("userId")int userId){
        List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
   }

}
