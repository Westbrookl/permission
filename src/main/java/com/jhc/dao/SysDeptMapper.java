package com.jhc.dao;

import com.jhc.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    /**
     * 选择出跟当前id不相等
     * 但是具有相同父Id和相同名称的部分
     * @param parentId
     * @param name
     * @param deptId
     * @return
     */
    Integer countByNameAndParentId(@Param("parentId") Integer parentId, @Param("name") String name, @Param("id") Integer deptId);

    List<SysDept>  getAllDept();

    List<SysDept> getChildDeptListByLevel(@Param("level") String level);
    void batchUpdateLevel(@Param("sysDeptList") List<SysDept> sysDeptList );
}