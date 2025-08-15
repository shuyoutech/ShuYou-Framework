package com.shuyoutech.common.web.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * 校验框架配置类
 *
 * @author YangChao
 * @date 2025-04-06 15:34
 **/
@Configuration
public class ValidatorConfig {

    /**
     * 遇到第一个错误后立即返回，而不是遍历完全部错误 快速验证模式，有第一个参数不满足条件直接返回
     */
    @Bean
    public Validator validator() {
        return Validation.byProvider(HibernateValidator.class).configure() //
                .addProperty("hibernate.validator.fail_fast", "true") //
                .buildValidatorFactory().getValidator();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator());
        return postProcessor;
    }

}
