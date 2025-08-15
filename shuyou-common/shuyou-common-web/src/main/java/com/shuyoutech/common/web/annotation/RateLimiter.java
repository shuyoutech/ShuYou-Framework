package com.shuyoutech.common.web.annotation;

import com.shuyoutech.common.web.enums.LimitTypeEnum;

import java.lang.annotation.*;

/**
 * @author YangChao
 * @date 2025-04-08 16:52
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key,支持使用Spring el表达式来动态获取方法上的参数值
     * 格式类似于  #code.id #{#code}
     */
    String key() default "";

    /**
     * 限流时间,单位秒
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 限流类型
     */
    LimitTypeEnum limitType() default LimitTypeEnum.DEFAULT;

    /**
     * 提示消息 支持国际化 格式为 {code}
     */
    String message() default "访问过于频繁，请稍候再试";

}
