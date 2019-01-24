package com.jhc.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jhc on 2019/1/24
 */
@Getter
@Setter
@ToString
public class RoleParam {

    private Integer id;

    @NotBlank(message = "角色名称不能为空")
    @Length(min=2,max = 20,message = "角色名称长度需要在2-20个字之间")
    private String name;

    @Max(value = 2,message = "角色类型不合法")
    @Min(value = 1,message = "角色类型不合法")
    private Integer type = 1;
    @NotNull(message = "角色状态不能为空")
    @Max(value = 1,message = "角色状态不合法")
    @Min(value = 0,message = "角色状态不合法")
    private Integer status;

    @Length(min = 0,max = 200,message = "备注应该在200字符以内")
    private String remark;

}
