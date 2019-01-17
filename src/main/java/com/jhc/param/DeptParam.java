package com.jhc.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jhc on 2019/1/16
 * 是用来更新或者新建部门时候的类
 */
@Getter
@Setter
@ToString
public class DeptParam {
    /**
     * 更新的时候主键可以为空
     */
    private Integer id;

    @NotBlank
    @Length(max = 15, min = 2, message = "部门名称应该在2-15字符之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "部门排序不能为空")
    private Integer seq;

    @Length(max = 150, message = "备注不能超过150个字符")
    private String remark;
}
