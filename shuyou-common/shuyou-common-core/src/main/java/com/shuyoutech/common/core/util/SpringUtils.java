package com.shuyoutech.common.core.util;

import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author YangChao
 * @date 2025-07-06 15:29
 **/
@Component
public class SpringUtils extends cn.hutool.extra.spring.SpringUtil {

    /**
     * 是否为生产环境
     *
     * @return 是否生产环境
     */
    public static boolean isProd() {
        String activeProfile = getActiveProfile();
        return StringUtils.equals("prod", activeProfile);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name bean名称
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    /**
     * 获取aop代理对象
     *
     * @param invoker 对象
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取spring上下文
     */
    public static ApplicationContext context() {
        return getApplicationContext();
    }

}
