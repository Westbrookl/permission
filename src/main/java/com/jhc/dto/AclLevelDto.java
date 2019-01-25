package com.jhc.dto;

import com.jhc.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * @author jhc on 2019/1/24
 * 这个类的目的是为了显示出列表中 当前模块是否具有该权限
 * 并且这个权限是否被选中。
 */
@Getter
@Setter
@ToString

public class AclLevelDto  extends SysAcl {

    //表示是否具有权限
    private boolean hasAcl = false;

    //表示这个权限是否被选中
    private boolean checked = false;

    public static AclLevelDto adapt(SysAcl acl){
        AclLevelDto dto = new AclLevelDto();
        BeanUtils.copyProperties(acl,dto);
        return dto;
    }
}
