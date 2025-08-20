package com.shuyoutech.common.redis.manager;

import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MessageSourceUtils;
import com.shuyoutech.common.redis.config.properties.CacheProperties;
import com.shuyoutech.common.redis.enums.CacheMsgTypeEnum;
import com.shuyoutech.common.redis.message.CacheMassage;
import com.shuyoutech.common.redis.util.CaffeineUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author YangChao
 * @date 2025-08-06 17:25
 **/
@Slf4j
public class RedisCaffeineCache extends AbstractValueAdaptingCache {

    private final String cacheName;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;

    public RedisCaffeineCache(String cacheName, RedisTemplate<String, Object> redisTemplate, CacheProperties cacheProperties) {
        super(cacheProperties.getAllowNull());
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
        this.cacheProperties = cacheProperties;
    }

    @Override
    protected Object lookup(Object key) {
        String cacheKey = this.cacheName + ":" + key;
        // 先从caffeine中查找
        Object obj = CaffeineUtils.get(cacheKey);
        if (null != obj) {
            return obj;
        }
        // 再从redis中查找
        obj = redisTemplate.opsForValue().get(cacheKey);
        if (null != obj) {
            CaffeineUtils.put(cacheKey, obj);
        }
        return obj;
    }

    @Override
    public String getName() {
        return this.cacheName;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        ReentrantLock lock = new ReentrantLock();
        try {
            // 加锁
            lock.lock();
            Object obj = lookup(key);
            if (null != obj) {
                return (T) obj;
            }
            // 没有找到
            obj = valueLoader.call();
            // 放入缓存
            put(key, obj);
            return (T) obj;
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        if (!isAllowNullValues() && null == value) {
            return;
        }
        String cacheKey = this.cacheName + ":" + key;

        // 使用 toStoreValue(value) 包装，解决caffeine不能存null的问题
        CaffeineUtils.put(cacheKey, toStoreValue(value));

        // null对象只存在caffeine中一份就够了，不用存redis了
        if (Objects.isNull(value)) {
            return;
        }

        redisTemplate.opsForValue().set(cacheKey, toStoreValue(value), cacheProperties.getRedisExpire(), TimeUnit.SECONDS);

        // 发送信息通知其他节点更新一级缓存
        CacheMassage cacheMassage = new CacheMassage();
        cacheMassage.setCacheName(this.cacheName);
        cacheMassage.setType(CacheMsgTypeEnum.UPDATE.getValue());
        cacheMassage.setKey(String.valueOf(key));
        cacheMassage.setValue(toStoreValue(value));
        cacheMassage.setMsgSource(MessageSourceUtils.getMsgSource());
        redisTemplate.convertAndSend(cacheProperties.getName(), cacheMassage);
    }

    @Override
    public void evict(Object key) {
        String cacheKey = this.cacheName + ":" + key;

        redisTemplate.delete(cacheKey);
        CaffeineUtils.evict(cacheKey);

        // 发送信息通知其他节点删除一级缓存
        CacheMassage cacheMassage = new CacheMassage();
        cacheMassage.setCacheName(this.cacheName);
        cacheMassage.setType(CacheMsgTypeEnum.DELETE.getValue());
        cacheMassage.setKey(String.valueOf(key));
        cacheMassage.setValue(null);
        cacheMassage.setMsgSource(MessageSourceUtils.getMsgSource());
        redisTemplate.convertAndSend(cacheProperties.getName(), cacheMassage);
    }

    @Override
    public void clear() {
        Set<String> keys = redisTemplate.keys(this.cacheName.concat(":*"));
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        CaffeineUtils.clear();
    }

    /**
     * 更新一级缓存
     *
     * @param key   key
     * @param value value
     */
    public void updateCaffeineCache(Object key, Object value) {
        String cacheKey = this.cacheName + ":" + key;
        CaffeineUtils.put(cacheKey, value);
    }

    /**
     * 删除一级缓存
     *
     * @param key key
     */
    public void evictCaffeineCache(Object key) {
        String cacheKey = this.cacheName + ":" + key;
        CaffeineUtils.evict(cacheKey);
    }

    /**
     * 获取所有内存值
     */
    public Map<Object, Object> cacheMap() {
        return CaffeineUtils.asMap();
    }

}
