package com.shuyoutech.common.redis.util;

import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.ConvertUtils;
import com.shuyoutech.common.core.util.SpringUtils;
import com.shuyoutech.common.redis.model.RedisMessage;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author YangChao
 * @date 2025-08-06 16:47
 **/
public class RedisUtils {

    private static final RedisTemplate<String, Object> redisTemplate = SpringUtils.getBean("redisTemplate");

    /**
     * 判断是否有key所对应的值，有则返回true，没有则返回false
     *
     * @param key 缓存的键
     * @return Boolean true 存在 false不存在
     */
    public static Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 查找匹配的key值，返回一个Set集合类型
     *
     * @param pattern 模糊查询key 支持通配符* 形如： *key*
     * @return keys Set集合
     */
    public static Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 删除单个key
     *
     * @param key 缓存的键
     */
    public static void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     *
     * @param key 键集合
     */
    public static void delete(String... key) {
        redisTemplate.delete(Arrays.asList(key));
    }

    /**
     * 删除多个key
     *
     * @param keys 键集合
     */
    public static void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 将key持久化保存,去除过期时间
     *
     * @param key 缓存的键
     * @return boolean 是否成功
     */
    public static Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 修改 key 的名称
     *
     * @param oldKey 旧的键
     * @param newKey 新的键
     */
    public static void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newKey 不存在时，将 oldKey 改名为 newKey
     *
     * @param oldKey 旧的键
     * @param newKey 新的键
     * @return boolean 是否成功
     */
    public static Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的类型: String类型 Hash类型 List类型 Set类型 ZSet类型
     *
     * @param key 缓存的键
     * @return 储存类型
     */
    public static DataType type(String key) {
        return redisTemplate.type(key);
    }

    /**
     * 将缓存的值写入缓存
     *
     * @param key   缓存的键
     * @param value 缓存的值
     * @param <T>   值对象类型
     */
    public static <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将value对象写入缓存
     * 如果key存在返回false；如果key不存在，就存入value返回true
     *
     * @param key   Redis键
     * @param value Redis值
     * @param <T>   对象类型
     * @return boolean 是否成功
     */
    public static <T> Boolean setIfAbsent(String key, T value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 将value对象写入缓存
     * 如果key存在返回false；如果key不存在，就存入value返回true
     *
     * @param key     Redis键
     * @param value   Redis值
     * @param timeout 失效时间
     * @param <T>     对象类型
     * @return boolean 是否成功
     */
    public static <T> Boolean setIfAbsent(String key, T value, Duration timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout);
    }

    /**
     * 将value对象写入缓存
     *
     * @param key     缓存的键值
     * @param value   缓存的值
     * @param timeout 失效时间
     * @param unit    失效单位
     * @param <T>     值对象类型
     */
    public static <T> void set(String key, T value, Long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 指定key缓存失效时间
     *
     * @param key     Redis键
     * @param timeout 失效时间
     * @param unit    失效单位
     */
    public static void expire(String key, Long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间
     *
     * @param key  Redis键
     * @param date 失效日期
     */
    public static void expireAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    /**
     * 根据key获取过期时间
     *
     * @param key Redis键
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static Long getExpire(String key) {
        return getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key  Redis键
     * @param unit 时间单位
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 追加到末尾
     *
     * @param key   Redis键
     * @param value Redis值
     * @return Integer
     */
    public static Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /**
     * 获得缓存的基本对象
     *
     * @param key    缓存键值
     * @param tClass 返回对象类型
     * @return 缓存键值对应的数据
     */
    public static <T> T get(String key, Class<T> tClass) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        Object object = operation.get(key);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 取得缓存字符串
     *
     * @param key 缓存键值
     * @return 字符串
     */
    public static String getString(String key) {
        return get(key, String.class);
    }

    /**
     * 取得缓存Integer
     *
     * @param key 缓存键值
     * @return Integer
     */
    public static Integer getInt(String key) {
        return get(key, Integer.class);
    }

    /**
     * 取得缓存Long
     *
     * @param key 缓存键值
     * @return Long
     */
    public static Long getLong(String key) {
        return get(key, Long.class);
    }

    /**
     * 获取double类型值
     *
     * @param key 缓存键值
     * @return Double
     */
    public static Double getDouble(String key) {
        return get(key, Double.class);
    }

    /**
     * 获取double类型值
     *
     * @param key 缓存键值
     * @return Boolean
     */
    public static Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    /**
     * 获取object对象缓存
     *
     * @param keys   keys
     * @param tClass 返回对象类型
     * @return 集合values
     */
    public static <T> List<T> get(Collection<String> keys, Class<T> tClass) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        List<Object> objects = operation.multiGet(keys);
        return ConvertUtils.toList(objects, tClass);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     *
     * @param key   Redis键
     * @param value Redis值
     * @param <T>   对象类型
     * @return 旧的值
     */
    public static <T> T getAndSet(String key, T value) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        Object object = operation.getAndSet(key, value);
        return ConvertUtils.convert(object, value.getClass());
    }

    /**
     * 批量添加键值对
     *
     * @param maps Redis 键值对
     */
    public static void multiSet(Map<String, Object> maps) {
        redisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     *
     * @param maps Redis 键值对
     * @return 之前已经存在返回false, 不存在返回true
     */
    public static Boolean multiSetIfAbsent(Map<String, Object> maps) {
        return redisTemplate.opsForValue().multiSetIfAbsent(maps);
    }

    /**
     * 通过索引获取列表中的元素
     *
     * @param key    Redis键
     * @param index  索引下标位置
     * @param tClass 返回对象类型
     * @return 对象
     */
    public static <T> T listIndex(String key, long index, Class<T> tClass) {
        Object object = redisTemplate.opsForList().index(key, index);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   Redis键
     * @param start 开始位置, 0是开始位置
     * @param end   结束位置, -1返回所有
     * @return list 对象集合
     */
    public static <T> List<T> listRange(String key, long start, long end, Class<T> tClass) {
        List<Object> objects = redisTemplate.opsForList().range(key, start, end);
        return ConvertUtils.toList(tClass, objects);
    }

    /**
     * 根据key获取对象
     *
     * @param key 缓存的键值
     * @param <T> 对象类型
     * @return 缓存键值对应的数据
     */
    public static <T> List<T> listRange(String key, Class<T> tClass) {
        return listRange(key, 0, -1, tClass);
    }

    /**
     * 根据key获取集合大小
     *
     * @param key Redis键
     * @return 集合数量
     */
    public static Long listSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 从左向右存压栈
     *
     * @param key   Redis键
     * @param value Redis值
     * @param <T>   对象类型
     * @return Long
     */
    public static <T> Long listLeftPush(String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 只有存在key对应的列表才能将这个value值插入到key所对应的列表中
     *
     * @param key   Redis键
     * @param value Redis值
     * @return Long
     */
    public static <T> Long listLeftPushIfPresent(String key, T value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 将集合数据合并到List缓存中
     *
     * @param key    Redis键
     * @param values Redis值
     * @param <T>    对象类型
     * @return Long
     */
    public static <T> Long listLeftPushAll(String key, Collection<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            return 0L;
        }
        return redisTemplate.opsForList().leftPushAll(key, values.toArray());
    }

    /**
     * 将单个数据添加到List缓存中
     *
     * @param key   Redis键
     * @param value Redis值
     * @param <T>   对象类型
     * @return Long
     */
    public static <T> Long listRightPush(String key, T value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将集合数据合并到List缓存中
     *
     * @param key    缓存的键值
     * @param values 待缓存的List数据
     * @param <T>    缓存的对象
     */
    public static <T> void listRightPushAll(String key, Collection<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        redisTemplate.opsForList().rightPushAll(key, values.toArray());
    }

    /**
     * 为已存在的列表添加值
     *
     * @param key   缓存的键值
     * @param value Redis值
     * @param <T>   缓存的对象
     * @return Long
     */
    public static <T> Long listRightPushIfPresent(String key, T value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @param key   缓存的键值
     * @param index 位置
     * @param value Redis值
     */
    public static <T> void listSet(String key, long index, T value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 从左出栈(返回最左边一个对象，且从redis中移除这个对象)
     *
     * @param key 缓存的键值
     * @return 对象
     */
    public static <T> T listLeftPop(String key, Class<T> tClass) {
        Object object = redisTemplate.opsForList().leftPop(key);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 从右出栈(返回最右边一个对象，且从redis中移除这个对象)
     *
     * @param key 缓存的键值
     * @return 对象
     */
    public static <T> T listRightPop(String key, Class<T> tClass) {
        Object object = redisTemplate.opsForList().rightPop(key);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 将源key的队列的右边的一个值删除，然后塞入目标key的队列的左边，返回这个值
     *
     * @param sourceKey      源key
     * @param destinationKey 目标key
     * @param <T>            对象类型
     * @return 对象
     */
    public static <T> T listRightPopAndLeftPush(String sourceKey, String destinationKey, Class<T> tClass) {
        Object object = redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 删除集合对象
     *
     * @param key   主键
     * @param count 删除个数 0表示删除所有
     * @param value 删除的对象
     * @return Long
     */
    public static Long listRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 截取集合元素长度，保留长度内的数据
     *
     * @param key   缓存的键
     * @param start 下标间的起始值
     * @param end   下标间的终止值
     */
    public static void listTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 判断Set中是否有该项的值
     *
     * @param key   缓存的键
     * @param value 缓存的值
     * @param <T>   值类型
     * @return true 存在 false不存在
     */
    public static <T> Boolean setIsMember(String key, T value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合的大小
     *
     * @param key 缓存的键
     * @return Long
     */
    public static Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 根据key获取set集合对象
     *
     * @param key    缓存的键
     * @param tClass 返回对象类型
     * @param <T>    值类型
     * @return Set 值集合
     */
    public static <T> Set<T> setMembers(String key, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().members(key);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 随机获取集合中的一个元素
     *
     * @param key    缓存的键
     * @param tClass 返回对象类型
     * @param <T>    值类型
     * @return 对象
     */
    public static <T> T setRandomMember(String key, Class<T> tClass) {
        Object object = redisTemplate.opsForSet().randomMember(key);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 随机获取集合中count个元素
     *
     * @param key    缓存的键
     * @param count  元素个数
     * @param tClass 返回对象类型
     * @param <T>    值类型
     * @return 对象
     */
    public static <T> List<T> setRandomMembers(String key, long count, Class<T> tClass) {
        List<Object> objects = redisTemplate.opsForSet().randomMembers(key, count);
        return ConvertUtils.toList(tClass, objects);
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     *
     * @param key    缓存的键
     * @param count  元素个数
     * @param tClass 返回对象类型
     * @param <T>    值类型
     * @return 集合对象
     */
    public static <T> Set<T> setDistinctRandomMembers(String key, long count, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().distinctRandomMembers(key, count);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 获取两个集合的交集
     *
     * @param key      缓存的键
     * @param otherKey 缓存的键
     * @param tClass   返回对象类型
     * @param <T>      值类型
     * @return 集合对象
     */
    public static <T> Set<T> setIntersect(String key, String otherKey, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().intersect(key, otherKey);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 获取key集合与多个集合的交集
     *
     * @param key       缓存的键
     * @param otherKeys 缓存的键
     * @param tClass    返回对象类型
     * @param <T>       值类型
     * @return 集合对象
     */
    public static <T> Set<T> setIntersect(String key, Collection<String> otherKeys, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().intersect(key, otherKeys);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * key集合与otherKey集合的交集存储到destKey集合中
     *
     * @param key      缓存的键
     * @param otherKey otherKey
     * @param destKey  destKey
     * @return Long
     */
    public static Long setIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的交集存储到destKey集合中
     *
     * @param key       缓存的键
     * @param otherKeys otherKeys
     * @param destKey   destKey
     * @return Long
     */
    public static Long setIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的并集
     *
     * @param key       缓存的键
     * @param otherKeys otherKeys
     * @param tClass    返回对象类型
     * @param <T>       值类型
     * @return 集合对象
     */
    public static <T> Set<T> setUnion(String key, String otherKeys, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().union(key, otherKeys);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 获取key集合与多个集合的并集
     *
     * @param key       缓存的键
     * @param otherKeys otherKeys
     * @param tClass    返回对象类型
     * @param <T>       值类型
     * @return 集合对象
     */
    public static <T> Set<T> setUnion(String key, Collection<String> otherKeys, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().union(key, otherKeys);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * key集合与otherKey集合的并集存储到destKey中
     *
     * @param key      缓存的键
     * @param otherKey otherKey
     * @param destKey  destKey
     * @return Long
     */
    public static Long setUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的并集存储到destKey中
     *
     * @param key       缓存的键
     * @param otherKeys otherKeys
     * @param destKey   destKey
     * @return Long
     */
    public static Long setUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key      缓存的键
     * @param otherKey otherKey
     * @param tClass   返回对象类型
     * @param <T>      值类型
     * @return 集合对象
     */
    public static <T> Set<T> setDifference(String key, String otherKey, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().difference(key, otherKey);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 获取key集合与多个集合的差集
     *
     * @param key       缓存的键
     * @param otherKeys otherKeys
     * @param tClass    返回对象类型
     * @param <T>       值类型
     * @return 集合对象
     */
    public static <T> Set<T> setDifference(String key, Collection<String> otherKeys, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForSet().difference(key, otherKeys);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * key集合与otherKey集合的差集存储到destKey中
     *
     * @param key      缓存的键
     * @param otherKey otherKey
     * @param destKey  destKey
     * @return Long
     */
    public static Long setDifference(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的差集存储到destKey中
     *
     * @param key       缓存的键
     * @param otherKeys otherKeys
     * @param destKey   destKey
     * @return Long
     */
    public static Long setDifference(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    /**
     * 将数据放入Set缓存
     *
     * @param key   缓存的键
     * @param value 缓存的值
     * @param <T>   对象类型
     * @return Long
     */
    public static <T> Long setAdd(String key, T value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 将数据放入Set缓存
     *
     * @param key    缓存的键
     * @param values 缓存集合值
     * @param <T>    对象类型
     * @return Long
     */
    public static <T> Long setAdd(String key, Set<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            return 0L;
        }
        return redisTemplate.opsForSet().add(key, values.toArray());
    }

    /**
     * 移除并返回集合的一个随机元素
     *
     * @param key    缓存的键
     * @param tClass 返回类型
     * @return 对象
     */
    public static <T> T setPop(String key, Class<T> tClass) {
        Object object = redisTemplate.opsForSet().pop(key);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 随机移除并返回多个元素
     *
     * @param key    缓存的键
     * @param count  移除个数
     * @param tClass 返回类型
     * @return 移除集合对象
     */
    public static <T> List<T> setPop(String key, long count, Class<T> tClass) {
        List<Object> objects = redisTemplate.opsForSet().pop(key, count);
        return ConvertUtils.toList(tClass, objects);
    }

    /**
     * 将元素value从一个集合移到另一个集合
     *
     * @param key     缓存的键
     * @param value   缓存的值
     * @param destKey 目标键
     * @return Boolean
     */
    public static <T> Boolean setMove(String key, T value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 删除Set集合对象
     *
     * @param key    缓存的键
     * @param values 删除的对象集合
     * @return 删除个数
     */
    public static <T> Long setRemove(String key, Collection<T> values) {
        return redisTemplate.opsForSet().remove(key, values.toArray());
    }

    /**
     * 匹配获取键值对
     * ScanOptions.NONE为获取全部键值对；
     * ScanOptions.scanOptions().match(“c”).build()匹配获取“c"键位的键值对,不能模糊匹配。
     *
     * @param key     缓存的键
     * @param options 游标选项
     * @return 游标匹配键值对
     */
    public static Cursor<Object> setScan(String key, ScanOptions options) {
        return redisTemplate.opsForSet().scan(key, options);
    }

    /**
     * 添加元素到变量中同时指定元素的分值 score值由小到大进行排列
     * 集合中对应元素已存在，会被覆盖，包括score
     *
     * @param key   缓存的键
     * @param value 缓存的值
     * @param score 分值
     * @return Boolean
     */
    public static <T> Boolean zsetAdd(String key, T value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 通过TypedTuple方式新增数据
     *
     * @param key    缓存的键
     * @param values TypedTuple值
     * @return 添加个数
     */
    public static Long zsetAdd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }

    /**
     * 批量移除元素根据元素值
     *
     * @param key    缓存的键
     * @param values 缓存的值集合
     * @return 移除个数
     */
    public static <T> Long zsetRemove(String key, Collection<T> values) {
        return redisTemplate.opsForZSet().remove(key, values.toArray());
    }

    /**
     * 修改变量中的元素的分值
     *
     * @param key   缓存的键
     * @param value 缓存的值
     * @param delta 分值
     * @return 分值
     */
    public static <T> Double zsetIncrementScore(String key, T value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 获取变量中元素的索引,下标开始位置为0 分数从小到大排序
     *
     * @param key   缓存的键
     * @param value 缓存的值
     * @return 0表示第一位
     */
    public static Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取倒序排列的索引值 分数从大到小排序
     *
     * @param key   缓存的键
     * @param value 缓存的值
     * @return 索引位置
     */
    public static Long zsetReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取变量指定区间的元素, 从小到大排序
     *
     * @param key   缓存的键
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return 元素集合
     */
    public static <T> Set<T> zsetRange(String key, long start, long end, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForZSet().range(key, start, end);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 获取变量指定区间的元素, 并且把score值也获取
     *
     * @param key   缓存的键
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return 元素集合
     */
    public static Set<ZSetOperations.TypedTuple<Object>> zsetRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素
     *
     * @param key 缓存的键
     * @param min 最小值
     * @param max 最大值
     * @return 元素集合
     */
    public static <T> Set<T> zsetRangeByScore(String key, double min, double max, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 根据Score值查询集合元素, 从小到大排序
     *
     * @param key 缓存的键
     * @param min 最小值
     * @param max 最大值
     * @return 元素集合
     */
    public static Set<ZSetOperations.TypedTuple<Object>> zsetRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * 从开始到结束的范围内获取一组元组，其中分数在分类集中的最小值和最大值之间
     *
     * @param key    缓存的键
     * @param min    最小值
     * @param max    最大值
     * @param offset 索引下标起始
     * @param count  个数
     * @return 元素集合
     */
    public static Set<ZSetOperations.TypedTuple<Object>> zsetRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 从高到低的排序集中获取从头(start)到尾(end)内的元素
     *
     * @param key   缓存的键
     * @param start 索引下标起始
     * @param end   索引下标终止
     * @return 元素集合
     */
    public static <T> Set<T> zsetReverseRange(String key, long start, long end, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 从开始(start)到结束(end)，从排序从高到低的排序集中获取元组的集
     *
     * @param key   缓存的键
     * @param start 索引下标起始
     * @param end   索引下标终止
     * @return 元素集合
     */
    public static Set<ZSetOperations.TypedTuple<Object>> zsetReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 从高到低的排序集中获取分数在最小和最大值之间的元素
     *
     * @param key 缓存的键
     * @param min 最小值
     * @param max 最大值
     * @return 元素集合
     */
    public static <T> Set<T> zsetReverseRangeByScore(String key, double min, double max, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 分数在最小和最大之间，从排序从高到低
     *
     * @param key 缓存的键
     * @param min 最小值
     * @param max 最大值
     * @return 元素集合
     */
    public static Set<ZSetOperations.TypedTuple<Object>> zsetReverseRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * 从开始到结束的范围内获取一组元组，其中分数在最小和最大之间，从排序集排序高到低
     *
     * @param key   缓存的键
     * @param min   最小值
     * @param max   最大值
     * @param start 索引下标起始
     * @param end   索引下标终止
     * @return 元素集合
     */
    public static <T> Set<T> zsetReverseRangeByScore(String key, double min, double max, long start, long end, Class<T> tClass) {
        Set<Object> objects = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 计算排序集中在最小和最大分数之间的元素数
     *
     * @param key 缓存的键
     * @param min 最小值
     * @param max 最大值
     * @return 元素数
     */
    public static Long zsetCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取集合大小
     *
     * @param key 缓存的键
     * @return 集合大小
     */
    public static Long zsetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合大小
     *
     * @param key 缓存的键
     * @return 集合大小
     */
    public static Long zsetZCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 使用键值从排序集中获取具有值的元素的分数
     *
     * @param key   缓存的键
     * @param value 元素值
     * @return 分数
     */
    public static Double zsetScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 使用键从排序集中删除开始和结束之间范围内的元素
     *
     * @param key   缓存的键
     * @param start 索引下标起始
     * @param end   索引下标终止
     * @return 删除个数
     */
    public static Long zsetRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 使用键从排序集中移除分数最小和最大值之间的元素
     *
     * @param key 缓存的键
     * @param min 最小值
     * @param max 最大值
     * @return 删除个数
     */
    public static Long zsetRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 在键和其他键上的联合排序集合，并将结果存储在目标destKey中(注意相交的元素分数相加)
     *
     * @param key      缓存的键
     * @param otherKey 缓存的其它键
     * @param destKey  缓存的目标键
     * @return 个数
     */
    public static Long zsetUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 给定的多个有序集的并集，并存储在新的 destKey中
     *
     * @param key       缓存的键
     * @param otherKeys 缓存的其它键集合
     * @param destKey   缓存的目标键
     * @return 个数
     */
    public static Long zsetUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 计算给定的一个与另一个有序集的交集并将结果集存储在新的有序集合 key 中
     *
     * @param key      缓存的键
     * @param otherKey 缓存的其它键
     * @param destKey  缓存的目标键
     * @return 个数
     */
    public static Long zsetIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
     *
     * @param key       缓存的键
     * @param otherKeys 缓存的其它键集合
     * @param destKey   缓存的目标键
     * @return 个数
     */
    public static Long zsetIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * 遍历集合，ScanOptions.NONE 所有对象集合
     *
     * @param key     缓存的键
     * @param options 浏览对象
     * @return 集合
     */
    public static Cursor<ZSetOperations.TypedTuple<Object>> zsetScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }

    /**
     * 判断变量中是否有指定的map键
     *
     * @param key  缓存的键
     * @param item 缓存的map键
     * @return Boolean true 存在 false不存在
     */
    public static <HK> Boolean hashExists(String key, HK item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   缓存的键
     * @param item  缓存的map键
     * @param value 缓存的map值
     */
    public static <HK, HV> void hashPut(String key, HK item, HV value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    /**
     * 以map集合的形式添加键值对
     *
     * @param key     缓存的键
     * @param dataMap 缓存的map键值对
     * @param <HK>    缓存的map键对象类型
     * @param <HV>    缓存的map值对象类型
     */
    public static <HK, HV> void hashPutAll(String key, Map<HK, HV> dataMap) {
        redisTemplate.opsForHash().putAll(key, dataMap);
    }

    /**
     * 如果变量值存在，在变量中可以添加不存在的的键值对，如果变量不存在，则新增一个变量，同时将键值对添加到该变量
     *
     * @param key   缓存的键
     * @param item  缓存的map键
     * @param value 缓存的map值
     * @param <HK>  缓存的map键对象类型
     * @param <HV>  缓存的map值对象类型
     */
    public static <HK, HV> Boolean hashPutIfAbsent(String key, HK item, HV value) {
        return redisTemplate.opsForHash().putIfAbsent(key, item, value);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key       缓存的键
     * @param item      缓存的map键
     * @param increment 累加值
     * @return Long
     */
    public <HK> Long hashIncrBy(String key, HK item, long increment) {
        return redisTemplate.opsForHash().increment(key, item, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key   缓存的键
     * @param item  缓存的map键
     * @param delta 累加值
     * @return Double
     */
    public <HK> Double hashIncrBy(String key, HK item, Double delta) {
        return redisTemplate.opsForHash().increment(key, item, delta);
    }

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key  缓存的键
     * @param item 缓存的map键
     * @return 缓存的map键值对象
     */
    public static <HK, HV> HV hashGet(String key, HK item, Class<HV> tClass) {
        Object object = redisTemplate.opsForHash().get(key, item);
        return ConvertUtils.convert(object, tClass);
    }

    /**
     * 获取hash中的所有键值对
     *
     * @param key 缓存的键
     * @return 缓存的map键值对象
     */
    public static <HK, HV> Map<HK, HV> hashEntries(String key) {
        BoundHashOperations<String, HK, HV> boundHashOperations = redisTemplate.boundHashOps(key);
        return boundHashOperations.entries();
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key   缓存的键
     * @param items 缓存的map键
     * @return 缓存的map键值对象集合
     */
    public static <HK, HV> List<HV> hashMultiGet(String key, Collection<HK> items) {
        BoundHashOperations<String, HK, HV> boundHashOperations = redisTemplate.boundHashOps(key);
        return boundHashOperations.multiGet(items);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key 缓存的键
     * @return 缓存的map键对象集合
     */
    public static <HK> Set<HK> hashKeys(String key, Class<HK> tClass) {
        Set<Object> objects = redisTemplate.opsForHash().keys(key);
        return ConvertUtils.toSet(tClass, objects);
    }

    /**
     * 获取指定变量中的hashMap值
     *
     * @param key 缓存的键
     * @return 缓存的map值对象集合
     */
    public static <HV> List<HV> hashValues(String key, Class<HV> tClass) {
        List<Object> objects = redisTemplate.opsForHash().values(key);
        return ConvertUtils.toList(tClass, objects);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key 缓存的键
     * @return 数量
     */
    public static Long hashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 迭代哈希表中的键值对
     *
     * @param key     缓存的键
     * @param options 迭代选项
     * @return 迭代集合对象
     */
    public static Cursor<Map.Entry<Object, Object>> hashScan(String key, ScanOptions options) {
        return redisTemplate.opsForHash().scan(key, options);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key      缓存的键
     * @param hashKeys 缓存的map键对象集合
     * @return 个数
     */
    public static Long hashDelete(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys.toArray());
    }

    /**
     * 递增操作,默认加1
     *
     * @param key 缓存的键
     * @return Long
     */
    public static Long incr(String key) {
        return incr(key, 1L);
    }

    /**
     * 递增操作
     *
     * @param key       缓存的键
     * @param increment 递增数量
     * @return Long
     */
    public static Long incr(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 递减操作,默认减1
     *
     * @param key 缓存的键
     * @return Long
     */
    public static Long decr(String key) {
        return decr(key, 1L);
    }

    /**
     * 递减操作
     *
     * @param key       缓存的键
     * @param increment 递减数量
     * @return Long
     */
    public static Long decr(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, -increment);
    }

    /**
     * 发布消息通知
     *
     * @param channel topic
     * @param message 消息体
     */
    public static void convertAndSend(String channel, RedisMessage message) {
        redisTemplate.convertAndSend(channel, message);
    }

}
