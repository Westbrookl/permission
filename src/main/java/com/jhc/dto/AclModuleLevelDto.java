package com.jhc.dto;

import com.google.common.collect.Lists;
import com.jhc.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author jhc on 2019/1/22
 *
 * 这个主要是为了这个列表 然后copy需要转换的对象的所有的属性
 */
@Getter
@Setter
@ToString
public class AclModuleLevelDto extends SysAclModule {
    private List<AclModuleLevelDto> aclModuleList = Lists.newArrayList();

    public static AclModuleLevelDto adapt(SysAclModule module){
        AclModuleLevelDto dto = new AclModuleLevelDto();
        BeanUtils.copyProperties(module,dto);
        return dto;
    }
}
