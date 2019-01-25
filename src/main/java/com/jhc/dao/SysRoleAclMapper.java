package com.jhc.dao;

import com.jhc.model.SysRoleAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    /**
     * 19-1-25
     */
    List<Integer> getAclIdListByRoleIdList(@Param("idList")List<Integer> idList);

    void deleteByRoleId(@Param("roleId")Integer roleId);

    void batchInsert(@Param("roleAclList")List<SysRoleAcl> roleAclList);

}