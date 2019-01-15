package com.jhc.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jhc on 2019/1/14
 * success 有三种方法 一个是接受 data msg
 * 一个是接受 data
 * 一个是什么都不接受的
 *
 * fail 只有一个方法 接受 msg 接受错误的信息
 *
 * 将这个对象做成个Map映射的方法
 */
@Getter
@Setter
public class JsonData {
    private boolean ret;
    private Object data;
    private String msg;

    public JsonData(boolean ret){
        this.ret = ret;
    }

    public static JsonData success(Object data,String msg){
        JsonData jsonData = new JsonData(true);
        jsonData.data =  data;
        jsonData.msg = msg;
        return jsonData;
    }

    public static JsonData success(Object object){
        JsonData jsonData = new JsonData(true);
        jsonData.data = object;
        return jsonData;
    }

    public static JsonData fail(String msg){
        JsonData jsonData = new JsonData(false);
        jsonData.msg = msg;
        return jsonData;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("ret",ret);
        map.put("data",data);
        map.put("msg",msg);
        return map;
    }
}
