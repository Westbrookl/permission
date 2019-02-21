package com.jhc.service;

import com.google.common.collect.Lists;
import com.jhc.beans.CacheKeyConstants;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysAclMapper;
import com.jhc.dao.SysRoleAclMapper;
import com.jhc.dao.SysRoleUserMapper;
import com.jhc.model.SysAcl;
import com.jhc.model.SysUser;
import com.jhc.util.JsonMapper;
import com.jhc.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Resource
    private SysCacheService sysCacheService;

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

        SysUser sysUser = RequestHolder.getCurrentUser();
        if(sysUser.getEmail().contains("admin")){
            return true;
        }
        return false;
    }

    /**
     * 判断当前的权限的问题
     * 得到当前用的所有的权限然后如果权限不存在则返回true
     * 然后查看当前用户所有的权限（如果所有的权限都失效则返回一个true）
     * 如果没有失效的话，则去判断是否含有这个权限如果有的话则返回true，否则返回一个false
     * @param url
     * @return
     */
    public boolean hasAclUrl(String url){
//        if(isSuperAdmin()){
//            return true;
//        }
        //如果这个权限不存在，默认可以访问
        List<SysAcl> aclList = sysAclMapper.getByUrl(url);
        if(CollectionUtils.isEmpty(aclList)){
            return true;
        }
        List<SysAcl> userAclList = getCurrentUserAclListFromCache();
        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        boolean hasValidAcl = false;
        //只要一个权限点有权限就认为有权限
        for(SysAcl acl:aclList){
            if(acl == null || acl.getStatus() != 1){
                continue;
            }
            hasValidAcl = true;
            if(userAclIdSet.contains(acl.getId())){
                return true;
            }
        }
        if(!hasValidAcl){
            return true;
        }
        return false;
    }

    public List<SysAcl> getCurrentUserAclListFromCache(){
        int userId = RequestHolder.getCurrentUser().getId();
        String cacheValue = sysCacheService.getFromCache(CacheKeyConstants.USER_ACLS,String.valueOf(userId));
        if(StringUtils.isBlank(cacheValue)){
            List<SysAcl> aclList = getAclListByUserId(userId);
            if(CollectionUtils.isNotEmpty(aclList)){
                sysCacheService.saveCache(JsonMapper.obj2String(aclList),600,CacheKeyConstants.USER_ACLS,String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.string2Object(cacheValue, new TypeReference<List<SysAcl>>() {
        });
    }
}
