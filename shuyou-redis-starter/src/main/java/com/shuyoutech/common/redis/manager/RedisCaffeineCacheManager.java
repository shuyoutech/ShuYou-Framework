package com.shuyoutech.common.redis.manager;

import com.shuyoutech.common.redis.config.properties.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YangChao
 * @date 2025-08-06 17:23
 **/
public class RedisCaffeineCacheManager implements CacheManager {

    private static final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;

    public RedisCaffeineCacheManager(RedisTemplate<String, Object> redisTemplate, CacheProperties cacheProperties) {
        this.redisTemplate = redisTemplate;
        this.cacheProperties = cacheProperties;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = cacheMap.get(name);
        if (null != cache) {
            return cache;
        }
        cache = new RedisCaffeineCache(name, redisTemplate, cacheProperties);
        Cache oldCache = cacheMap.putIfAbsent(name, cache);
        return null == oldCache ? cache : oldCache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }

}
