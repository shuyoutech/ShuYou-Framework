package com.shuyoutech.common.redis.manager;

import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MessageSourceUtils;
import com.shuyoutech.common.redis.config.properties.CacheProperties;
import com.shuyoutech.common.redis.enums.CacheMsgTypeEnum;
import com.shuyoutech.common.redis.message.CacheMassage;
import com.shuyoutech.common.redis.util.CaffeineUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Redis + Caffeine 二级缓存实现
 * <p>
 * 采用两级缓存架构：
 * <ul>
 *     <li>一级缓存：Caffeine（本地内存缓存），提供极速访问</li>
 *     <li>二级缓存：Redis（分布式缓存），提供数据共享和持久化</li>
 * </ul>
 * <p>
 * 缓存策略：
 * <ul>
 *     <li>读取：先查Caffeine，未命中再查Redis，命中后回填Caffeine</li>
 *     <li>写入：同时写入Caffeine和Redis，并通知其他节点更新本地缓存</li>
 *     <li>删除：同时删除Caffeine和Redis，并通知其他节点删除本地缓存</li>
 * </ul>
 *
 * @author YangChao
 * @since 2025-08-06 17:25
 */
@Slf4j
public class RedisCaffeineCache extends AbstractValueAdaptingCache {

    /**
     * 缓存名称
     */
    private final String cacheName;

    /**
     * Redis模板，用于操作Redis缓存
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存配置属性
     */
    private final CacheProperties cacheProperties;

    /**
     * 实例级别的锁，用于防止并发加载相同key的缓存
     * 采用双重检查锁定模式，避免缓存击穿
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 构造函数
     *
     * @param cacheName       缓存名称
     * @param redisTemplate   Redis模板
     * @param cacheProperties 缓存配置属性
     */
    public RedisCaffeineCache(String cacheName, RedisTemplate<String, Object> redisTemplate, CacheProperties cacheProperties) {
        super(cacheProperties.getAllowNull());
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
        this.cacheProperties = cacheProperties;
    }

    /**
     * 查找缓存值
     * <p>
     * 查找顺序：Caffeine -> Redis
     * 如果Redis命中，会将值回填到Caffeine中
     *
     * @param key 缓存键
     * @return 缓存值，未找到返回null
     */
    @Override
    protected Object lookup(@NonNull Object key) {
        String cacheKey = this.cacheName + ":" + key;
        // 先从 Caffeine 一级缓存中查找
        Object obj = CaffeineUtils.get(cacheKey);
        if (null != obj) {
            return obj;
        }
        // 一级缓存未命中，从 Redis 二级缓存中查找
        obj = redisTemplate.opsForValue().get(cacheKey);
        if (null != obj) {
            // 回填到一级缓存，提升后续访问速度
            CaffeineUtils.put(cacheKey, obj);
        }
        return obj;
    }

    /**
     * 获取缓存名称
     *
     * @return 缓存名称
     */
    @Override
    public String getName() {
        return this.cacheName;
    }

    /**
     * 获取原生缓存对象
     *
     * @return 当前缓存实例
     */
    @Override
    public Object getNativeCache() {
        return this;
    }

    /**
     * 获取缓存值，如果不存在则通过valueLoader加载
     * <p>
     * 采用双重检查锁定模式（Double-Check Locking）：
     * <ol>
     *     <li>第一次检查：不加锁快速检查缓存是否存在</li>
     *     <li>加锁：如果缓存不存在，获取锁</li>
     *     <li>第二次检查：再次检查缓存（可能在等待锁期间其他线程已加载）</li>
     *     <li>加载数据：如果仍未命中，执行valueLoader加载数据并缓存</li>
     * </ol>
     * <p>
     * 此机制可以有效防止缓存击穿（Cache Penetration）和重复加载
     *
     * @param key         缓存键
     * @param valueLoader 值加载器，当缓存不存在时调用
     * @param <T>         值类型
     * @return 缓存值，加载失败返回null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        // 第一次检查：不加锁快速检查缓存是否存在
        Object obj = lookup(key);
        if (null != obj) {
            return (T) obj;
        }

        // 加锁，防止并发加载相同key导致缓存击穿
        lock.lock();
        try {
            // 第二次检查：可能在等待锁的过程中，其他线程已经加载了
            obj = lookup(key);
            if (null != obj) {
                return (T) obj;
            }
            // 缓存未命中，执行加载逻辑
            obj = valueLoader.call();
            // 将加载的数据放入缓存
            put(key, obj);
            return (T) obj;
        } catch (Exception exception) {
            log.error("加载缓存失败, cacheName: {}, key: {}", cacheName, key, exception);
        } finally {
            lock.unlock();
        }
        return null;
    }

    /**
     * 写入缓存
     * <p>
     * 写入策略：
     * <ul>
     *     <li>如果配置不允许null值且value为null，直接返回</li>
     *     <li>写入Caffeine一级缓存（使用toStoreValue包装，解决Caffeine不能存null的问题）</li>
     *     <li>如果value不为null，写入Redis二级缓存并设置过期时间</li>
     *     <li>发送消息通知其他节点更新本地 Caffeine缓存</li>
     * </ul>
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    @Override
    public void put(@NonNull Object key, Object value) {
        // 如果配置不允许null值且value为null，直接返回
        if (!isAllowNullValues() && null == value) {
            return;
        }
        String cacheKey = this.cacheName + ":" + key;

        // 使用 toStoreValue(value) 包装，解决Caffeine不能存null的问题
        CaffeineUtils.put(cacheKey, toStoreValue(value));

        // null对象只存在Caffeine中一份就够了，不用存Redis了
        if (Objects.isNull(value)) {
            return;
        }

        // 写入Redis二级缓存，并设置过期时间
        redisTemplate.opsForValue().set(cacheKey, toStoreValue(value), cacheProperties.getRedisExpire(), TimeUnit.SECONDS);

        // 发送消息通知其他节点更新一级缓存，保持多节点缓存一致性
        CacheMassage cacheMassage = new CacheMassage();
        cacheMassage.setCacheName(this.cacheName);
        cacheMassage.setType(CacheMsgTypeEnum.UPDATE.getValue());
        cacheMassage.setKey(String.valueOf(key));
        cacheMassage.setValue(toStoreValue(value));
        cacheMassage.setMsgSource(MessageSourceUtils.getMsgSource());
        redisTemplate.convertAndSend(cacheProperties.getName(), cacheMassage);
    }

    /**
     * 删除指定 key 的缓存
     * <p>
     * 删除策略：
     * <ul>
     *     <li>同时删除 Redis和Caffeine中的缓存</li>
     *     <li>发送消息通知其他节点删除本地 Caffeine缓存</li>
     * </ul>
     *
     * @param key 缓存键
     */
    @Override
    public void evict(@NonNull Object key) {
        String cacheKey = this.cacheName + ":" + key;

        // 删除 Redis二级缓存
        redisTemplate.delete(cacheKey);
        // 删除 Caffeine一级缓存
        CaffeineUtils.evict(cacheKey);

        // 发送消息通知其他节点删除一级缓存，保持多节点缓存一致性
        CacheMassage cacheMassage = new CacheMassage();
        cacheMassage.setCacheName(this.cacheName);
        cacheMassage.setType(CacheMsgTypeEnum.DELETE.getValue());
        cacheMassage.setKey(String.valueOf(key));
        cacheMassage.setValue(null);
        cacheMassage.setMsgSource(MessageSourceUtils.getMsgSource());
        redisTemplate.convertAndSend(cacheProperties.getName(), cacheMassage);
    }

    /**
     * 清空所有缓存
     * <p>
     * 清空策略：
     * <ul>
     *     <li>删除 Redis中所有匹配该缓存名称的key</li>
     *     <li>清空本地 Caffeine缓存</li>
     * </ul>
     * <p>
     * 注意：此操作会清空整个缓存组的所有数据，请谨慎使用
     */
    @Override
    public void clear() {
        // 查找 Redis中所有匹配的key
        Set<String> keys = redisTemplate.keys(this.cacheName.concat(":*"));
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        // 清空本地 Caffeine缓存
        CaffeineUtils.clear();
    }

    /**
     * 更新一级缓存（Caffeine）
     * <p>
     * 用于接收其他节点的缓存更新通知，仅更新本地Caffeine缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void updateCaffeineCache(Object key, Object value) {
        String cacheKey = this.cacheName + ":" + key;
        CaffeineUtils.put(cacheKey, value);
    }

    /**
     * 删除一级缓存（Caffeine）
     * <p>
     * 用于接收其他节点的缓存删除通知，仅删除本地Caffeine缓存
     *
     * @param key 缓存键
     */
    public void evictCaffeineCache(Object key) {
        String cacheKey = this.cacheName + ":" + key;
        CaffeineUtils.evict(cacheKey);
    }

    /**
     * 获取所有本地内存缓存值
     * <p>
     * 返回Caffeine缓存中的所有键值对，用于监控和调试
     *
     * @return 缓存键值对映射
     */
    public Map<Object, Object> cacheMap() {
        return CaffeineUtils.asMap();
    }

}
