package com.shuyoutech.common.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.shuyoutech.common.cache.config.properties.CacheProperties;
import com.shuyoutech.common.cache.manager.RedisCaffeineCacheManager;
import com.shuyoutech.common.cache.message.CacheMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.concurrent.TimeUnit;

/**
 * @author YangChao
 * @date 2025-08-06 17:16
 **/
@Slf4j
@EnableCaching
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    @Bean
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache() {
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder();
        caffeineBuilder.initialCapacity(cacheProperties.getCaffeineInit());
        caffeineBuilder.maximumSize(cacheProperties.getCaffeineMax());
        caffeineBuilder.expireAfterWrite(cacheProperties.getCaffeineExpire(), TimeUnit.SECONDS);
        return caffeineBuilder.build();
    }

    @Bean
    public CacheManager cacheManager() {
        return new RedisCaffeineCacheManager(redisTemplate, cacheProperties);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        CacheMessageListener cacheMessageListener = new CacheMessageListener(redisTemplate, cacheManager());
        container.addMessageListener(cacheMessageListener, new PatternTopic(cacheProperties.getName()));
        return container;
    }

    private final CacheProperties cacheProperties;
    private final RedisTemplate<String, Object> redisTemplate;

}
