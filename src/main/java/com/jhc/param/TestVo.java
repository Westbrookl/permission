package com.jhc.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @author jhc on 2019/1/14
 * 对于字符串 判断为 NotBlank
 * 对于数字 判断为NotNull
 * <p>
 * 对于对象判断为 NotEmpty
 */
@Getter
@Setter
public class TestVo {

    @NotBlank
    private String msg;

    @NotNull(message = "id不能为空")
    @Max(value = 10, message = "id 不能大于10")
    @Min(value = 0, message = "id 不能小于0")
    private Integer id;
    @NotEmpty
    private List<String> str;
}
