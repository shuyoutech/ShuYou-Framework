package com.shuyoutech.common.core.util;

import com.shuyoutech.common.core.exception.BusinessException;
import io.github.linpeilie.Converter;

import java.util.List;
import java.util.Map;

import static com.shuyoutech.common.core.enums.ErrorCodeEnum.UNKNOWN;

/**
 * @author YangChao
 * @date 2025-07-08 15:56
 **/
public class MapstructUtils {

    private final static Converter CONVERTER = SpringUtils.getBean(Converter.class);

    /**
     * 将 T 类型对象，转换为 targetType 类型的对象并返回
     *
     * @param source     数据来源实体
     * @param targetType 描述对象 转换后的对象
     * @return targetType
     */
    public static <T, V> V convert(T source, Class<V> targetType) {
        if (ObjectUtils.isNull(source) || ObjectUtils.isNull(targetType)) {
            throw new BusinessException(UNKNOWN);
        }
        return CONVERTER.convert(source, targetType);
    }

    /**
     * 将 T 类型对象，按照配置的映射字段规则，给 target 类型的对象赋值并返回 desc 对象
     *
     * @param source 数据来源实体
     * @param target 转换后的对象
     * @return target
     */
    public static <T, V> V convert(T source, V target) {
        if (ObjectUtils.isNull(source) || ObjectUtils.isNull(target)) {
            throw new BusinessException(UNKNOWN);
        }
        return CONVERTER.convert(source, target);
    }

    /**
     * 将 T 类型的集合，转换为 desc 类型的集合并返回
     *
     * @param sourceList 数据来源实体列表
     * @param targetType 描述对象 转换后的对象
     * @return targetType
     */
    public static <T, V> List<V> convert(List<T> sourceList, Class<V> targetType) {
        if (ObjectUtils.isNull(targetType) || CollectionUtils.isEmpty(sourceList)) {
            throw new BusinessException(UNKNOWN);
        }
        return CONVERTER.convert(sourceList, targetType);
    }

    /**
     * 将 Map 转换为 beanClass 类型的集合并返回
     *
     * @param map       数据来源
     * @param beanClass bean类
     * @return bean对象
     */
    public static <T> T convert(Map<String, Object> map, Class<T> beanClass) {
        if (MapUtils.isEmpty(map) || ObjectUtils.isNull(beanClass)) {
            throw new BusinessException(UNKNOWN);
        }
        return CONVERTER.convert(map, beanClass);
    }

}
