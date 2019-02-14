package com.jhc.service;

import com.google.common.collect.Lists;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysAclMapper;
import com.jhc.dao.SysRoleAclMapper;
import com.jhc.dao.SysRoleUserMapper;
import com.jhc.model.SysAcl;
import com.jhc.model.SysUser;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jhc on 2019/1/24
 */
@Service
public class SysCoreService {
    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    /**
     * 这个是获取当前用户所有的权限
     * 因为在创建用户的使用并没有涉及到权限的内容
     * 但是在这里确实要用到权限的内容
     * 所有就想到了表的 关联
     * 通过表的关联 首先会获得当前用户的所有的角色
     * 然后根据当前角色获得所有的权限Id
     * 最后根据sysAclMapper来获得所有的权限
     *
     * @return
     */
    public List<SysAcl> getCurrentUserAclList(){
        int userId = RequestHolder.getCurrentUser().getId();
        return getAclListByUserId(userId);
    }

    /**
     * 通过用户的id去查找所有的权限需要的是表的关联
     *  通过用户的Id找到对应的部门
     *  然后通过部门获得是所有的权限
     * @param userId
     * @return
     */
    public List<SysAcl> getAclListByUserId(Integer userId){
        if(isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        List<Integer> roleId = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(roleId)){
            return Lists.newArrayList();
        }
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(roleId);
        if(CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        List<SysAcl> aclList = sysAclMapper.getByIdList(aclIdList);
        return aclList;
    }

    public List<SysAcl> getRoleAclList(int roleId){
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if(CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }


    public boolean isSuperAdmin(){
        return true;
    }
}
