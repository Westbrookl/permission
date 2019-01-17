package com.jhc.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jhc on 2019/1/16
 *
 * 需要说明的有两点
 * 1 对于level的计算是 当前的父level+“，”+父id
 *
 * 2 导入了新的jar包，StringUtils这个包确实是简化了我对于字符串的判断直接isBlank就判断了是否为空和字符串长度要大于0
 *
 */
public class LevelUntil {
    public final static  String SEPERATOR = ",";

    public final static String ROOT = "0";

    public static String calculateLevel(String parentLevel, int parentId){
        if(StringUtils.isBlank(parentLevel)){
            return ROOT;
        }else{
            return StringUtils.join(parentLevel,SEPERATOR,parentId);
        }
    }
}
