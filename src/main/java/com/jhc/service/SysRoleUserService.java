package com.jhc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysRoleUserMapper;
import com.jhc.dao.SysUserMapper;
import com.jhc.model.SysRoleUser;
import com.jhc.model.SysUser;
import com.jhc.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author jhc on 2019/2/14
 */
@Service
public class SysRoleUserService {

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    public List<SysUser> getUserByRoleId(int roleId){
        List<Integer> userIdList = sysRoleUserMapper.getUsersIdList(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return  sysUserMapper.getUserByIdList(userIdList);
    }

    /**
     * 改变当前角色下面的所有的用户
     * 首先获取当前角色下面所有的用户
     * 判断当前的角色用户是否与要改变的相同
     * @param roleId
     * @param userIdList
     */
    public void changeRoleUsers(int roleId,List<Integer> userIdList){
        List<Integer> originUserIdList = sysRoleUserMapper.getUsersIdList(roleId);
        //判断两个集合是否相等，通过Set集合来判断做差集，如果差集为空则正面两个完全相同
        if(originUserIdList.size() == userIdList.size()){
            Set<Integer> originUserIdSet = Sets.newHashSet(originUserIdList);
            Set<Integer> userIdSet = Sets.newHashSet(userIdList);
            originUserIdSet.removeAll(userIdSet);
            if(originUserIdList.isEmpty()){
                return;
            }
        }
        //更新用户角色与用户的列表
       updateRoleUsers(roleId,userIdList);
    }
    @Transactional
    private void updateRoleUsers(int roleId,List<Integer> userIdList){
        //首先删除当前角色下面的所有的用户
        sysRoleUserMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return;
        }
        //然后把角色与用户插入到表当中去
        List<SysRoleUser> roleUserList = Lists.newArrayList();
        for(Integer userId:userIdList){
            SysRoleUser roleUser = SysRoleUser.builder().roleId(roleId).userId(userId).operator(RequestHolder.getCurrentUser().getUsername()).operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleUserList.add(roleUser);
        }
        sysRoleUserMapper.batchInsert(roleUserList);
    }


}
