package com.jhc.param;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * @author jhc on 2019/1/22
 */
@Getter
@Setter
@ToString
public class SysAclModuleParam {

    private Integer id;

    @NotBlank(message = "权限模块名称不能为空")
    @Length(min = 2, max = 20, message = "权限模块的名称需要在2-20字符之间")
    private String name;


    private Integer parentId = 0;

    @NotNull(message = "权限模块的排序顺序不能为空")
    private Integer seq;

    @NotNull(message = "权限模块的状态不能为空")
    @Min(value = 0, message = "状态不合法")
    @Max(value = 1, message = "状态不合法")
    private Integer status;

    @Length(max = 255, message = "备注的长度不能超过255个字符")
    private String remark;

}
