package com.shuyoutech.common.core.util;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author YangChao
 * @date 2025-07-06 12:16
 **/
public class ConvertUtils extends Convert {

    /**
     * 转换值为指定类型
     *
     * @param <T>   目标类型
     * @param type  类型
     * @param value 被转换的值
     * @return 对象
     */
    public static <T> T convert(Object value, Type type) {
        if (value instanceof String) {
            if (JSON.isValidObject((String) value)) {
                return JSON.parseObject((String) value, type);
            } else if (JSON.isValidArray((String) value)) {
                return convert(JSON.parseArray((String) value, JSONObject.class), type, null);
            }
        }
        return convert(value, type, null);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>          目标类型
     * @param type         类型
     * @param value        被转换的值
     * @param defaultValue 默认值
     * @return 对象
     */
    public static <T> T convert(Object value, Type type, T defaultValue) {
        return Convert.convert(type, value, defaultValue);
    }

    /**
     * 转换为ArrayList
     *
     * @param value       被转换的值
     * @param elementType 集合中元素类型
     * @param <T>         对象类型
     * @return 集合对象
     */
    public static <T> List<T> toList(Object value, Class<T> elementType) {
        if (null == value) {
            return CollectionUtils.newArrayList();
        }
        String jsonString = JSON.toJSONString(value);
        if (JSON.isValidArray(jsonString)) {
            return JSON.parseArray(jsonString, elementType);
        }
        return Convert.toList(elementType, value);
    }

    /**
     * 将满足实现ConvertTo接口的Collection转换成目标Collection.
     *
     * @param sourceList 源Collection
     * @param <E>        转换源类型
     * @param <T>        转换目标类型
     * @return 包含转换目标的Collection
     */
    public static <E, T> List<T> convert(Collection<E> sourceList, Function<E, T> converter) {
        List<T> destinationList = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(sourceList)) {
            return destinationList;
        }
        sourceList.forEach(item -> destinationList.add(converter.apply(item)));
        return destinationList;
    }

}
