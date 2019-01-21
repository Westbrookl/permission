package com.jhc.dao;

import com.jhc.beans.PageQuery;
import com.jhc.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    int countByTelephone(@Param("telephone")String telephone,@Param("id") Integer id);

    int countByEmail(@Param("email")String  email,@Param("id") Integer id);

    SysUser findByKeyWord(@Param("username")String username);

    int countByDeptId(@Param("deptId") int deptId);

    List<SysUser> getPageByDeptId(@Param("deptId") int deptId, @Param("pageQuery")PageQuery pageQuery);
}