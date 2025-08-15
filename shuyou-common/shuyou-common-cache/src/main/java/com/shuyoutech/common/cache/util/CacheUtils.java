package com.shuyoutech.common.cache.util;

import com.shuyoutech.common.cache.manager.RedisCaffeineCache;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.core.util.SpringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Map;

/**
 * @author YangChao
 * @date 2025-08-06 17:25
 **/
public class CacheUtils {

    private static final CacheManager CACHE_MANAGER = SpringUtils.getBean(CacheManager.class);

    /**
     * 获取缓存值
     *
     * @param cacheName 缓存组名称
     * @param key       缓存key
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String cacheName, Object key) {
        Cache.ValueWrapper wrapper = CACHE_MANAGER.getCache(cacheName).get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    /**
     * 保存缓存值
     *
     * @param cacheName 缓存组名称
     * @param key       缓存key
     * @param value     缓存值
     */
    public static void put(String cacheName, Object key, Object value) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        if (null != cache) {
            cache.put(key, value);
        }
    }

    /**
     * 删除缓存值
     *
     * @param cacheName 缓存组名称
     * @param key       缓存key
     */
    public static void evict(String cacheName, Object key) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        if (null != cache) {
            cache.evict(key);
        }
    }

    /**
     * 清空缓存值
     *
     * @param cacheName 缓存组名称
     */
    public static void clear(String cacheName) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        if (null != cache) {
            cache.clear();
        }
    }

    /**
     * 获取所有cacheNames
     */
    public static Collection<String> getCacheNames() {
        return CACHE_MANAGER.getCacheNames();
    }

    /**
     * 获取cacheName对应key
     */
    public static Map<Object, Object> getKeyByCacheName(String cacheName) {
        Map<Object, Object> result = MapUtils.newHashMap();
        RedisCaffeineCache cache = (RedisCaffeineCache) CACHE_MANAGER.getCache(cacheName);
        if (null == cache) {
            return result;
        }
        Map<Object, Object> cacheMap = cache.cacheMap();
        for (Object key : cacheMap.keySet()) {
            if (String.valueOf(key).startsWith(cacheName)) {
                result.put(key, cacheMap.get(key));
            }
        }
        return result;
    }

}
