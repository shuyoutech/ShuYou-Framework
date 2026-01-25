package com.shuyoutech.common.web.config;

import com.shuyoutech.common.web.aspect.RateLimitAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YangChao
 * @since 2025-04-08 17:49
 **/
@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimitAspect rateLimiterAspect() {
        return new RateLimitAspect();
    }

}
