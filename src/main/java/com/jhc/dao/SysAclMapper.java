package com.jhc.dao;

import com.jhc.beans.PageQuery;
import com.jhc.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    int countByNameAndId(@Param("aclModuleId")Integer aclModuleId,@Param("name")String name,@Param("id")Integer id);

    List<SysAcl> getAll();

    int countByAclModuleId(@Param("aclModuleId")Integer aclModuleId);

    List<SysAcl> getPageByAclModuleId(@Param("aclModuleId")Integer aclModuleId, @Param("pageQuery")PageQuery pageQuery);
    /**
     *
     */
    List<SysAcl>  getByIdList(@Param("idList")List<Integer> idList);
}