package com.jhc.dao;

import com.jhc.model.SysRoleUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    /**
     * 19-1-25
     */
    List<Integer> getRoleIdListByUserId(@Param("userId") Integer userId);


    List<Integer> getUsersIdList(@Param("roleId") Integer roleId);
    void deleteByRoleId(@Param("roleId") int roleId);
    void batchInsert(@Param("roleUserList")List<SysRoleUser> roleUserList);
}