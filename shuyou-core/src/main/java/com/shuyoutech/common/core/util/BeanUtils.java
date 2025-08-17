package com.shuyoutech.common.core.util;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.service.SimpleConverter;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.Converter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 基于 cglib bean拷贝工具类
 *
 * @author YangChao
 * @date 2025-07-06 12:10
 **/
public class BeanUtils {

    private static final SimpleCache<String, BeanCopier> BEAN_COPIER_CACHE = new SimpleCache<>();

    private static String generateKey(Class<?> srcClass, Class<?> targetClass) {
        return StringUtils.join(StringConstants.UNDERLINE, srcClass.getName(), targetClass.getName());
    }

    private static BeanCopier getBeanCopier(Class<?> sourceClass, Class<?> targetClass) {
        String beanKey = generateKey(sourceClass, targetClass);
        return BEAN_COPIER_CACHE.get(beanKey, () -> BeanCopier.create(sourceClass, targetClass, true));
    }

    /**
     * 拷贝源对象到目标对象.
     *
     * @param source      源对象实体
     * @param targetClass 目标对象实体
     * @return 目标所在的对象中的字段对象
     */
    public static <T, V> V copy(T source, Class<V> targetClass) {
        return copy(source, targetClass, new SimpleConverter());
    }

    /**
     * 拷贝源对象到目标对象,支持自定义规则
     *
     * @param source       源对象实体
     * @param targetClass  目标对象类型
     * @param useConverter 自定义规则
     */
    public static <T, V> V copy(T source, Class<V> targetClass, Converter useConverter) {
        if (null == source || null == targetClass) {
            return null;
        }
        Class<?> srcClass = source.getClass();
        BeanCopier copier = getBeanCopier(srcClass, targetClass);
        V targetObject = ReflectUtil.newInstance(targetClass);
        copier.copy(source, targetObject, useConverter);
        return targetObject;
    }

    /**
     * 拷贝源对象到目标对象.
     *
     * @param sourceList  源对象实体
     * @param targetClass 目标对象实体
     * @return 目标所在的对象中的字段对象
     */
    public static <T, V> List<V> copyList(Collection<T> sourceList, Class<V> targetClass) {
        List<V> list = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(sourceList)) {
            return list;
        }
        return StreamUtils.toList(sourceList, source -> copy(source, targetClass));
    }

    /**
     * 将对象装换为map
     *
     * @param beanClass 对象
     * @return 对象
     */
    public static <T> Map<String, Object> beanToMap(T beanClass) {
        return beanToMap(beanClass, true);
    }

    /**
     * 将List<T>转换为List<Map>
     *
     * @param sourceList 集合对象
     * @return 集合Map
     */
    public static <T> List<Map<String, Object>> beanToMapList(List<T> sourceList) {
        List<Map<String, Object>> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(sourceList)) {
            return result;
        }
        return StreamUtils.toList(sourceList, BeanUtils::beanToMap);
    }

    /**
     * 将对象装换为map
     *
     * @param beanClass  对象
     * @param ignoreNull 是否忽略空值
     * @return Map
     */
    public static <T> Map<String, Object> beanToMap(T beanClass, boolean ignoreNull) {
        Map<String, Object> result = MapUtils.newHashMap();
        if (null == beanClass) {
            return result;
        }
        BeanMap beanMap = BeanMap.create(beanClass);
        if (null == beanMap) {
            return result;
        }
        for (Object key : beanMap.keySet()) {
            if (ignoreNull) {
                if (null != beanMap.get(key)) {
                    result.put(key.toString(), beanMap.get(key));
                }
            } else {
                result.put(key.toString(), beanMap.get(key));
            }
        }
        return result;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map         Map
     * @param targetClass 目标对象
     * @param <T>         class的泛型
     * @return 对象
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> targetClass) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        Field[] fields = ReflectUtil.getFields(targetClass);
        if (ArrayUtil.isEmpty(fields)) {
            return null;
        }
        T instance = ReflectUtil.newInstance(targetClass);
        Arrays.stream(fields) //
                .filter(field -> map.containsKey(field.getName())) //
                .forEach(item -> map.put(item.getName(), ConvertUtils.convert(map.get(item.getName()), item.getGenericType())));
        BeanMap beanMap = BeanMap.create(instance);
        beanMap.putAll(map);
        return instance;
    }

    /**
     * 将List<Map>转换为List<T>
     *
     * @param sourceList  集合Map
     * @param targetClass 对象类型
     * @return 集合对象
     */
    public static <T> List<T> mapToBeanList(List<Map<String, Object>> sourceList, Class<T> targetClass) {
        List<T> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(sourceList)) {
            return result;
        }
        return StreamUtils.toList(sourceList, source -> mapToBean(source, targetClass));
    }

}
