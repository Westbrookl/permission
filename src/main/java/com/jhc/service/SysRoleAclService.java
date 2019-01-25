package com.jhc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysRoleAclMapper;
import com.jhc.model.SysRoleAcl;
import com.jhc.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author jhc on 2019/1/25
 */
@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    /**
     * 改变当前用户的权限的值
     * 首先获得当前用户的所有的权限
     * 用来比较来个权限内容是否相同 取出两个部门的权限的Id
     * 然后取交集如果为空的话说明没有改变
     * 如果不为空的话，说明权限确实被改变了
     * 然后进行更新
     *
     * @param roleId
     * @param aclIdList
     */
    public void changeRoleAcls(int roleId, List<Integer> aclIdList){
        List<Integer> originAclResults = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if(originAclResults.size() == aclIdList.size()){
            Set<Integer> originSet = Sets.newHashSet(originAclResults);
            Set<Integer> newSet = Sets.newHashSet(aclIdList);
             originSet.removeAll(newSet);
            if(CollectionUtils.isEmpty(originSet)){
                return ;
            }
        }
        updateRoleAcls(roleId,aclIdList);
    }

    /**
     * 更新权限的做法是：删除当前部门的所有的权限
     * 然后批量更新权限内容
     *
     * @param roleId
     * @param aclIdList
     */
    @Transactional
    public void updateRoleAcls(int roleId,List<Integer> aclIdList){
        sysRoleAclMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(aclIdList)){
            return;
        }
        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for(Integer Id:aclIdList){
            SysRoleAcl roleAcl = SysRoleAcl.builder().roleId(roleId).aclId(Id).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }

}
