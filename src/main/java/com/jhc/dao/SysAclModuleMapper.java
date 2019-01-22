package com.jhc.dao;

import com.jhc.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);


    List<SysAclModule> getChildAclModuleByLevel(@Param("level")String level);

    void batchUpdateLevel(@Param("moduleList")List<SysAclModule> moduleList);

    int countByNameAndLevel(@Param("name")String name,@Param("aclModuleId")Integer aclModuleId,@Param("parentId")Integer parentId);

    List<SysAclModule> getAllAclModule();
}