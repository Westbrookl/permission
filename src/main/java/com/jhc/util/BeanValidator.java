package com.jhc.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jhc.exception.ParamException;
import org.apache.commons.collections4.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * @author jhc on 2019/1/14
 * 验证单个对象的类型
 * ValidatorFactory valida = Validation.buildDefaultValidationFacatory();
 * Validator validator = valida.getValidator()
 * 得到一个验证的集合
 * 然后判断验证的集合是否为空 如果为空的话便返回一个空map
 *
 * 如果不为空的话，便把所有的异常信息放入到一个map里面去
 *
 */
public class BeanValidator {
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public static <T> Map<String, String> validate(T t, Class... groups) {
        Validator validator = validatorFactory.getValidator();
        Set validateResult = validator.validate(t, groups);
        if (validateResult.isEmpty()) {
            return Collections.emptyMap();
        } else {
            LinkedHashMap errors = Maps.newLinkedHashMap();
            Iterator iterator = validateResult.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation violation = (ConstraintViolation) iterator.next();
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errors;
        }
    }

    public static <T> Map<String,String> validateList(Collection<?> collection){
        Preconditions.checkNotNull(collection);//如果为空的话会抛出异常，不为空的继续执行，使用的也是Guava包
        Iterator iterator = collection.iterator();
        Map errors;
        /**
         * 下面断代码的思想是
         * 遍历需要判断的对象集合
         * 创建一个map
         * 是有一个异常便抛出
         * 如果都没有异常便返回一个空的map
         */
        do{
            if(!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object,new Class[0]);
        }while(errors.isEmpty());

        return errors;
    }

    public static Map<String,String> validateObject(Object arg,Object...args){
        if(args != null && args.length > 0){
            return validateList(Lists.asList(arg,args));//使用的Guava包构成一个List的数组
        }else{
            return validate(arg,new Class[0]);
        }
    }

    /**
     * 这个是用来对象是否有不满足的条件如果有的话便抛出异常
     *
     * @param param
     * @throws ParamException
     */
    public static void check(Object param) throws ParamException{
        Map<String,String> map =  BeanValidator.validateObject(param);//使用的common包
        if(MapUtils.isNotEmpty(map)){
            throw new ParamException(map.toString());
        }
    }
}
