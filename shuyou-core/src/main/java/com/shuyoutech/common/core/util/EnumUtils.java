package com.shuyoutech.common.core.util;

import com.shuyoutech.common.core.enums.BaseEnum;

/**
 * @author YangChao
 * @date 2025-07-06 13:44
 **/
public class EnumUtils extends cn.hutool.core.util.EnumUtil {

    /**
     * 根据value获取枚举label
     *
     * @param clazz 枚举类
     * @param value 枚举值
     * @return 枚举文本
     */
    public static <E extends BaseEnum<K, V>, K, V> V getLabelByValue(Class<E> clazz, K value) {
        BaseEnum<K, V> e = getEnumByValue(clazz, value);
        return e == null ? null : e.getLabel();
    }

    /**
     * 根据text获取枚举value
     *
     * @param clazz 枚举类
     * @param label 枚举文本
     * @return 枚举值
     */
    public static <E extends BaseEnum<K, V>, K, V> K getValueByLabel(Class<E> clazz, V label) {
        BaseEnum<K, V> e = getEnumByLabel(clazz, label);
        return e == null ? null : e.getValue();
    }

    /**
     * 根据value获取枚举对象
     *
     * @param clazz 枚举类
     * @param value 枚举值
     * @return 枚举对象
     */
    public static <E extends BaseEnum<K, V>, K, V> E getEnumByValue(Class<E> clazz, K value) {
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据text获取枚举对象
     *
     * @param clazz 枚举类
     * @param label 枚举文本
     * @return 枚举对象
     */
    public static <E extends BaseEnum<K, V>, K, V> E getEnumByLabel(Class<E> clazz, V label) {
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (e.getLabel().equals(label)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 判断枚举类是否包含value
     *
     * @param clazz 枚举类
     * @param value 枚举值
     * @return boolean
     */
    public static <E extends BaseEnum<K, V>, K, V> boolean contain(Class<E> clazz, K value) {
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (e.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
