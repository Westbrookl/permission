package com.jhc.util;

import java.util.Date;
import java.util.Random;

/**
 * @author jhc on 2019/1/20
 * 这个类的主要的作用是生成一个随机的字符串和数字 交替出现的字符串当做密码
 * 具体的做法是创建一个字符串数组和一个数字数组
 * 然后随机的生成字符串
 * 首先定义密码的长度 然后再根据内容去创建内容
 *
 */
public class PasswordUtil {
    public final static String[] word = {
            "a","b","c","d","e","f","g",
            "h","j","k","m","n","p","q",
            "r","s","t","u","v","w","x",
            "y","z","A","B","C","D","E",
            "F","H","J","K","M","N","R",
            "S","T","U","V","W","X","Y","Z"
    };
    public final static String[] number = {
            "2","3","4","5","6","7","8","9"
    };

    public static String randomPassword(){
        StringBuffer sb  = new StringBuffer();
        Random random = new Random(new Date().getTime());
        boolean flag = false;
        int length = random.nextInt(3)+8;//密码的长度是8-10位
        for(int i=0;i<length;i++){
            if(flag){
                sb.append(number[random.nextInt(number.length)]);
            }else {
                sb.append(word[random.nextInt(word.length)]);
            }
            flag = !flag;
        }
        return sb.toString();

    }
}
