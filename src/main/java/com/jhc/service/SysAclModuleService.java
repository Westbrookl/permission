package com.jhc.service;

import com.jhc.common.RequestHolder;
import com.jhc.dao.SysAclMapper;
import com.jhc.dao.SysAclModuleMapper;
import com.jhc.exception.ParamException;
import com.jhc.model.SysAclModule;
import com.jhc.param.SysAclModuleParam;
import com.jhc.util.BeanValidator;
import com.jhc.util.IpUtil;
import com.jhc.util.LevelUntil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author jhc on 2019/1/22
 */
@Service
public class SysAclModuleService {
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    public void save(SysAclModuleParam param){
        BeanValidator.check(param);
            if(checkExist(param.getName(),param.getId(),param.getParentId())){
            throw new ParamException("同一层级下面不能存有相同名称的权限模块");
        }
        SysAclModule module = SysAclModule.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        module.setLevel(LevelUntil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        module.setOperator(RequestHolder.getCurrentUser().getUsername());
        module.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        module.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(module);
    }
    public void update(SysAclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId(),param.getParentId())){
            throw new ParamException("同一层级下面不能存有相同名称的权限模块");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        if(before == null){
            throw new ParamException("当前用户不存在");
        }
        SysAclModule after = SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setLevel(LevelUntil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before,after);
    }

    /**
     * 添加到事物里面去
     * 其实就是把当前层级的所有前缀替换成新变换的事物里面去
     *
     *
     *
     * @param before
     * @param after
     */
    @Transactional
    public void updateWithChild(SysAclModule before,SysAclModule after){
        String oldPrefix = before.getLevel();
        String newPrefix = after.getLevel();
        if(!oldPrefix.equals(newPrefix)){
            List<SysAclModule> moduleList = sysAclModuleMapper.getChildAclModuleByLevel(oldPrefix);
            if(!CollectionUtils.isEmpty(moduleList)){
                for(SysAclModule module:moduleList){
                    String level = module.getLevel();
                    if(level.indexOf(oldPrefix) == 0){
                        level = newPrefix + level.substring(oldPrefix.length());
                        module.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(moduleList);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);

    }

    /**
     * 在当前的层级下面不能存在相同的名称的权限模块
     * @param name
     * @param aclModuleId
     * @param parentId
     * @return
     */
    public boolean checkExist(String name,Integer aclModuleId,Integer parentId){
//        System.out.println(sysAclModuleMapper.countByNameAndLevel(name,aclModuleId,parentId));
        return sysAclModuleMapper.countByNameAndLevel(name,aclModuleId,parentId) > 0;
    }

    public String getLevel(Integer parentId){
        SysAclModule module = sysAclModuleMapper.selectByPrimaryKey(parentId);
        if(module == null){
            return  null;
        }else{
            return module.getLevel();
        }
    }


}
