package com.jhc.service;

import com.google.common.base.Preconditions;
import com.jhc.beans.PageQuery;
import com.jhc.beans.PageResult;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysAclMapper;
import com.jhc.exception.ParamException;
import com.jhc.model.SysAcl;
import com.jhc.param.SysAclParam;
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
public class SysAclService {
    @Resource
    private SysAclMapper sysAclMapper;

    public void save(SysAclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("同一个权限模块下面存在相同的权限点名称");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        acl.setOperateTime(new Date());
        sysAclMapper.insertSelective(acl);
    }

    public void update(SysAclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("同一个权限模块下面存在相同的权限点名称");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(param.getId());
//        if(before == null){
//            throw new ParamException("要更新的权限点不存在");
//        }
        Preconditions.checkNotNull(before,"要更新的权限点不存在");
        SysAcl after = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysAclMapper.updateByPrimaryKeySelective(after);
    }
    public boolean checkExist(Integer aclModuleId,String name,Integer aclId){
        return sysAclMapper.countByNameAndId(aclModuleId,name,aclId) > 0;
    }

    public PageResult<SysAcl> gePageByAclModuleId(Integer id, PageQuery pageQuery){
//        int count =  sysAclMapper.getAllCount(id);
//        List<SysAcl> aclList = sysAclMapper.getPage(pageQuery);
//        PageResult result = new PageResult(count,aclList);
        BeanValidator.check(pageQuery);
        int count = sysAclMapper.countByAclModuleId(id);
        if(count > 0){
            List<SysAcl> list = sysAclMapper.getPageByAclModuleId(id,pageQuery);
            return  PageResult.<SysAcl>builder().data(list).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }
    public List<SysAcl> list(){
        return sysAclMapper.getAll();
    }
}
