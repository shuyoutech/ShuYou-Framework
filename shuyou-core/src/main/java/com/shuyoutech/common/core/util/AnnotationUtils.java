package com.shuyoutech.common.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author YangChao
 * @date 2025-07-05 20:43
 **/
public class AnnotationUtils extends cn.hutool.core.annotation.AnnotationUtil {

    /**
     * 获取指定注解
     *
     * @param <A>            注解类型
     * @param annotationType 注解类型
     * @return 注解对象
     */
    public static <A extends Annotation> A getAnnotation(Class<?> targetClass, Method method, Class<A> annotationType) {
        A annotation = getAnnotation(method, annotationType);
        if (null != annotation) {
            return annotation;
        }
        return getAnnotation(targetClass, annotationType);
    }

}
