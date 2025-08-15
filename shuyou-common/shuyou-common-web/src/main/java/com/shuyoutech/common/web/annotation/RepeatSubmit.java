package com.shuyoutech.common.web.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解防止表单重复提交
 *
 * @author YangChao
 * @date 2025-04-06 16:45
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    int interval() default 3000;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}