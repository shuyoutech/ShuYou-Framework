package com.shuyoutech.common.cache.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.shuyoutech.common.core.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentMap;

/**
 * @author YangChao
 * @date 2025-08-14 17:15
 **/
@Slf4j
public class CaffeineUtils {

    private static final Cache<Object, Object> CAFFEINE_CACHE = SpringUtils.getBean("caffeineCache");

    /**
     * 获取所有缓存值
     */
    public static ConcurrentMap<Object, Object> asMap() {
        return CAFFEINE_CACHE.asMap();
    }

    /**
     * 获取缓存值
     *
     * @param cacheKey 缓存key
     */
    public static Object get(String cacheKey) {
        return CAFFEINE_CACHE.getIfPresent(cacheKey);
    }

    /**
     * 保存缓存值
     *
     * @param cacheKey 缓存key
     * @param value    缓存值
     */
    public static void put(String cacheKey, Object value) {
        CAFFEINE_CACHE.put(cacheKey, value);
    }

    /**
     * 删除缓存值
     *
     * @param cacheKey 缓存key
     */
    public static void evict(String cacheKey) {
        CAFFEINE_CACHE.invalidate(cacheKey);
    }

    /**
     * 清空缓存值
     */
    public static void clear() {
        CAFFEINE_CACHE.invalidateAll();
    }

}
