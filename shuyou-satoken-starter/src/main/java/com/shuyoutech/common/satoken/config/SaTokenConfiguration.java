package com.shuyoutech.common.satoken.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import com.shuyoutech.common.satoken.handler.SaTokenExceptionHandler;
import com.shuyoutech.common.satoken.service.StpInterfaceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author YangChao
 * @date 2025-07-09 11:36
 **/
@AutoConfiguration
public class SaTokenConfiguration {

    /**
     * Sa-Token 整合 jwt
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 权限接口实现
     */
    @Bean
    public StpInterface stpInterface() {
        return new StpInterfaceImpl();
    }

    /**
     * 异常处理器
     */
    @Bean
    public SaTokenExceptionHandler saTokenExceptionHandler() {
        return new SaTokenExceptionHandler();
    }

}
