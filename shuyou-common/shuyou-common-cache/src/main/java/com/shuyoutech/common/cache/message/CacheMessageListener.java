package com.shuyoutech.common.cache.message;

import com.shuyoutech.common.cache.enums.CacheMsgTypeEnum;
import com.shuyoutech.common.cache.manager.RedisCaffeineCache;
import com.shuyoutech.common.core.util.MessageSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author YangChao
 * @date 2025-08-06 17:24
 **/
@Slf4j
public class CacheMessageListener implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    public CacheMessageListener(RedisTemplate<String, Object> redisTemplate, CacheManager cacheManager) {
        super();
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 接收通知，进行处理
        CacheMassage msg = (CacheMassage) redisTemplate.getValueSerializer().deserialize(message.getBody());
        if (null == msg) {
            return;
        }
        // 如果是本机发出的消息，那么不进行处理
        if (MessageSourceUtils.getMsgSource().equals(msg.getMsgSource())) {
            return;
        }
        // 获取缓存对象
        RedisCaffeineCache cache = (RedisCaffeineCache) cacheManager.getCache(msg.getCacheName());
        if (null == cache) {
            return;
        }
        // 处理缓存
        if (CacheMsgTypeEnum.UPDATE.getValue().equals(msg.getType())) {
            cache.updateCaffeineCache(msg.getKey(), msg.getValue());
        } else if (CacheMsgTypeEnum.DELETE.getValue().equals(msg.getType())) {
            cache.evictCaffeineCache(msg.getKey());
        }
    }

}
