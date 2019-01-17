package com.jhc.dto;

import com.jhc.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jhc on 2019/1/16
 */
@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept {
    private List<DeptLevelDto> deptList = new ArrayList<>();

    public static DeptLevelDto adapt(SysDept dept) {
        DeptLevelDto deptLevelDto = new DeptLevelDto();
        BeanUtils.copyProperties(dept, deptLevelDto);//把所有的属性复制一遍
        return deptLevelDto;
    }


}
